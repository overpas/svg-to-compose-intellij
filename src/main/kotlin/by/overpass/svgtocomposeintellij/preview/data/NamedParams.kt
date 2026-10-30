package by.overpass.svgtocomposeintellij.preview.data

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import java.lang.reflect.Field
import java.lang.reflect.Modifier

private const val DEFAULT_BLEND_MODE = 5 // SrcIn
private const val DEFAULT_AUTO_MIRROR = false
private const val DEFAULT_STROKE_LINE_CAP = 0 // Butt
private const val DEFAULT_STROKE_JOIN = 0 // Miter
private const val DEFAULT_PATH_FILL_TYPE = 0 // Non-zero

internal class NamedParams(private val values: List<Pair<String, String>>) {

    private val defaultColor = Color.Unspecified.value.toLong()

    fun string(
        name: String,
        default: String,
    ): String =
        value(name)?.run { substringBefore("\"").substringAfter("\"") } ?: default

    fun float(name: String): Float =
        parseFloat(value(name) ?: error("Missing parameter $name"))

    fun floatOrDefault(
        name: String,
        default: Float,
    ): Float =
        value(name)?.let(::parseFloat) ?: default

    fun color(name: String): Long =
        value(name)?.let(::parseColor) ?: defaultColor

    fun blendMode(name: String): Int =
        value(name)?.let { text -> staticField(BlendMode::class.java, text.substringAfter('.')) }
            ?.getInt(null)
            ?: DEFAULT_BLEND_MODE

    fun autoMirror(name: String): Boolean =
        value(name)?.toBoolean() ?: DEFAULT_AUTO_MIRROR

    fun brush(name: String): Brush? =
        value(name)
            ?.takeIf { text -> "SolidColor" in text }
            ?.let { text -> SolidColor(Color(parseColor(text.substringAfter("SolidColor(").substringBeforeLast(")")))) }

    fun strokeCap(name: String): Int =
        value(name)?.let { text -> staticField(StrokeCap::class.java, text.substringAfter('.')) }
            ?.getInt(null)
            ?: DEFAULT_STROKE_LINE_CAP

    fun strokeJoin(name: String): Int =
        value(name)?.let { text -> staticField(StrokeJoin::class.java, text.substringAfter('.')) }
            ?.getInt(null)
            ?: DEFAULT_STROKE_JOIN

    fun pathFillType(name: String): Int =
        value(name)?.let { text -> staticField(PathFillType::class.java, text.substringAfter('.')) }
            ?.getInt(null)
            ?: DEFAULT_PATH_FILL_TYPE

    private fun value(name: String): String? =
        values.firstOrNull { (key, _) -> key == name }?.second

    private fun parseFloat(text: String): Float =
        text.removeSuffix(".dp").toFloat()

    // Can be Color(0x00000000) or Color.Black
    private fun parseColor(text: String): Long =
        when {
            "(" in text -> hexToLongColor(text.substringAfter("(").substringBefore(")"))
            "." in text -> staticField(Color::class.java, text.substringAfter("."))?.getLong(null) ?: defaultColor
            else -> defaultColor
        }

    private fun staticField(
        type: Class<*>,
        name: String,
    ): Field? =
        type.declaredFields
            .find { field -> Modifier.isStatic(field.modifiers) && field.name == name }
            ?.apply { isAccessible = true }
}
