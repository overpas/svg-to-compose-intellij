package by.overpass.svgtocomposeintellij.preview.data

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.DefaultGroupName
import androidx.compose.ui.graphics.vector.DefaultPathName
import androidx.compose.ui.graphics.vector.DefaultStrokeLineMiter
import androidx.compose.ui.graphics.vector.DefaultStrokeLineWidth
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.unit.dp
import java.io.File
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val DEFAULT_BLEND_MODE = 5 // SrcIn
private const val DEFAULT_AUTO_MIRROR = false
private const val DEFAULT_STROKE_LINE_CAP = 0 // Butt
private const val DEFAULT_STROKE_JOIN = 0 // Miter
private const val DEFAULT_PATH_FILL_TYPE = 0 // Non-zero
private const val DEFAULT_FILL_ALPHA = 0.5f
private const val DEFAULT_STROKE_ALPHA = 0.5f

interface IconDataParser {

    suspend fun parse(): Result<IconData>
}

@Suppress("TooManyFunctions")
class KotlinFileIconDataParser(
    private val file: File,
) : IconDataParser {

    private val builderPattern =
        ("=\\s*Builder\\((?<builderParams>[\\S\\s]*)\\)\\.apply\\s*\\{(?<builderContent>[\\S\\s]*)\\}\\s*\\n\\s*\\" +
                ".build\\(\\)").toRegex()
    private val namedParamsPattern =
        "(?<paramName>\\w+)[\\s\\n]*=[\\s\\n]*(?<paramValue>[\\w.\"()]+)(,|([\\s\\n]*\\)))".toRegex()
    private val pathOperationPattern = "path\\s*(?<pathParams>\\([\\S\\s]*)?\\{(?<pathContent>[\\S\\s]*)\\}".toRegex()

    private val defaultColor = Color.Unspecified.value.toLong()

    private data class BuilderDefinition(
        val fullText: String,
        val builderParams: String,
        val builderContent: String,
    )

    private data class NamedParams(
        val name: String,
        val value: String,
    ) {

        fun getFloatValue(): Float {
            return if (value.endsWith(".dp")) {
                value.substringBefore(".dp")
                    .toFloat()
                    .dp
                    .value
            } else {
                value.toFloat()
            }
        }
    }

    private data class PathOperation(
        val fullText: String,
        val pathParams: String?,
        val pathBuilderContent: String,
    )

    override suspend fun parse(): Result<IconData> = withContext(Dispatchers.Default) {
        runCatching<IconData> {
            val text = file.readText()
            val name = getIconName(text)
            val builderDefinition = getBuilderDefinition(text) ?: throw IllegalStateException("Failed to parse icon")
            val builderParams = getNamedParams(builderDefinition.builderParams)
            val builder = createBuilderWithParams(builderParams)
            val pathOperations = getPathOperations(builderDefinition.builderContent)
            pathOperations.forEach { pathOperation ->
                val params = pathOperation.pathParams?.let(::getNamedParams) ?: emptyList()
                val pathBuilderContentOperations = getPathBuilderContentOperations(pathOperation.pathBuilderContent)
                builder.drawPathWithParams(params) {
                    pathBuilderContentOperations.forEach { pathBuilderContentOperation ->
                        perform(pathBuilderContentOperation)
                    }
                }
            }
            val imageVector = builder.build()
            IconData(
                name = name,
                imageVector = imageVector,
            )
        }
    }.onFailure {
        it.printStackTrace()
    }

    private fun getIconName(text: String): String {
        return imageVectorDeclarationPattern.find(text)?.groupValues?.get(2)
            ?: throw IllegalStateException("Failed to parse icon name")
    }

    private fun getBuilderDefinition(text: String): BuilderDefinition? {
        val groups = builderPattern.find(text)?.groups
        val fullText = groups?.get(0)?.value
        val builderParams = groups?.get("builderParams")?.value
        val builderContent = groups?.get("builderContent")?.value
        if (fullText == null || builderParams == null || builderContent == null) {
            return null
        }
        return BuilderDefinition(
            fullText = fullText,
            builderParams = "$builderParams)",
            builderContent = builderContent.trim { it.isWhitespace() || it == '\n' },
        )
    }

    private fun getNamedParams(text: String): List<NamedParams> {
        return namedParamsPattern.findAll(text)
            .toList()
            .map { result ->
                val name = result.groups["paramName"]?.value!!
                val value = result.groups["paramValue"]?.value!!
                NamedParams(
                    name = name,
                    value = value,
                )
            }
    }

    @Suppress("MagicNumber")
    private fun createBuilderWithParams(namedParams: List<NamedParams>): ImageVector.Builder {
        val builderClass = ImageVector.Builder::class.java
        val builderConstructor = builderClass.declaredConstructors
            .find { constructor ->
                constructor.parameterCount == 8 && Modifier.isPrivate(constructor.modifiers)
            }!!
            .apply {
                isAccessible = true
            }
        val newInstance = builderConstructor.newInstance(
            namedParams.find { it.name == "name" }.getStringNameOrDefault(DefaultGroupName),
            namedParams.find { it.name == "defaultWidth" }?.getFloatValue()!!,
            namedParams.find { it.name == "defaultHeight" }?.getFloatValue()!!,
            namedParams.find { it.name == "viewportWidth" }?.getFloatValue()!!,
            namedParams.find { it.name == "viewportHeight" }?.getFloatValue()!!,
            namedParams.find { it.name == "tintColor" }.getColorLongValueOrDefault(defaultColor),
            namedParams.find { it.name == "tintBlendMode" }.getTintBlendModeIntValueOrDefault(DEFAULT_BLEND_MODE),
            namedParams.find { it.name == "autoMirror" }.getAutoMirrorBooleanValueOrDefault(DEFAULT_AUTO_MIRROR)
        )
        return newInstance as ImageVector.Builder
    }

    private fun NamedParams?.getStringNameOrDefault(default: String): String {
        return if (this != null) {
            value.substringBefore("\"").substringAfter("\"")
        } else {
            default
        }
    }

    private fun NamedParams?.getColorLongValueOrDefault(default: Long): Long {
        return if (this != null) {
            getColorLongValueOrDefault(value, default)
        } else {
            default
        }
    }

    private fun NamedParams?.getTintBlendModeIntValueOrDefault(default: Int): Int {
        return if (this != null) {
            val blendModes = BlendMode::class.java
                .declaredFields
                .filter { Modifier.isStatic(it.modifiers) }
            val currentValue = if (value.contains('.')) {
                value.substringAfter('.')
            } else {
                value
            }
            blendModes.find { it.name == currentValue }
                ?.apply {
                    isAccessible = true
                }
                ?.getInt(null)
                ?: default
        } else {
            default
        }
    }

    private fun NamedParams?.getAutoMirrorBooleanValueOrDefault(default: Boolean): Boolean {
        return if (this != null) {
            value.toBoolean()
        } else {
            default
        }
    }

    // Can be Color(0x00000000) or Color.Black
    private fun getColorLongValueOrDefault(text: String, default: Long): Long {
        return if (text.contains("(")) {
            text.substringAfter("(")
                .substringBefore(")")
                .let(::hexToLongColor)
        } else if (text.contains(".")) {
            val colorText = text.substringAfter(".")
            Color::class.java
                .declaredFields
                .find { it.name == colorText }
                ?.apply {
                    isAccessible = true
                }
                ?.getLong(null)
                ?: default
        } else {
            default
        }
    }

    private fun getPathOperations(text: String): List<PathOperation> {
        return pathOperationPattern.findAll(text)
            .toList()
            .map { result ->
                val fullText = result.groups[0]?.value
                val pathParams = result.groups["pathParams"]
                    ?.value
                    ?.trimStart { it.isWhitespace() || it == '(' || it == '\n' }
                val pathContent = result.groups["pathContent"]
                    ?.value
                    ?.trim { it.isWhitespace() || it == '\n' }
                if (fullText == null || pathContent == null) {
                    return emptyList()
                }
                PathOperation(
                    fullText = fullText,
                    pathParams = pathParams,
                    pathBuilderContent = pathContent + "\n",
                )
            }
    }

    private inline fun ImageVector.Builder.drawPathWithParams(
        namedParams: List<NamedParams>,
        crossinline pathBuilderContent: PathBuilder.() -> Unit = {},
    ) {
        val pathMethod = Class.forName("androidx.compose.ui.graphics.vector.ImageVectorKt")
            .declaredMethods
            .find {
                it.name.contains("path") && it.parameters.last().type.name == "kotlin.jvm.functions.Function1"
            }
            ?.apply {
                isAccessible = true
            }
        val functionClass = Class.forName("kotlin.jvm.functions.Function1")
        val pathBuilderProxy = Proxy.newProxyInstance(
            functionClass.getClassLoader(),
            arrayOf<Class<*>>(functionClass)
        ) { _, _, args -> // Handle the invocations
            val pathBuilder = args[0] as PathBuilder
            pathBuilder.pathBuilderContent()
        }
        pathMethod?.invoke(
            null,
            this,
            namedParams.find { it.name == "name" }.getStringNameOrDefault(DefaultPathName),
            namedParams.find { it.name == "fill" }.getBrushOrDefault(null),
            namedParams.find { it.name == "fillAlpha" }?.getFloatValue() ?: DEFAULT_FILL_ALPHA,
            namedParams.find { it.name == "stroke" }.getBrushOrDefault(null),
            namedParams.find { it.name == "strokeAlpha" }?.getFloatValue() ?: DEFAULT_STROKE_ALPHA,
            namedParams.find { it.name == "strokeLineWidth" }?.getFloatValue() ?: DefaultStrokeLineWidth,
            namedParams.find { it.name == "strokeLineCap" }.getStrokeLineCapIntOrDefault(DEFAULT_STROKE_LINE_CAP),
            namedParams.find { it.name == "strokeLineJoin" }.getStrokeJoinIntOrDefault(DEFAULT_STROKE_JOIN),
            namedParams.find { it.name == "strokeLineMiter" }?.getFloatValue() ?: DefaultStrokeLineMiter,
            namedParams.find { it.name == "pathFillType" }.getPathFillTypeIntOrDefault(DEFAULT_PATH_FILL_TYPE),
            pathBuilderProxy
        )
    }

    private fun NamedParams?.getBrushOrDefault(default: Brush?): Brush? {
        return if (this != null) {
            if (value.contains("SolidColor")) {
                val colorValue = value.substringAfter("SolidColor(").substringBeforeLast(")")
                SolidColor(
                    Color(
                        NamedParams("temp", colorValue)
                            .getColorLongValueOrDefault(defaultColor),
                    ),
                )
            } else {
                default
            }
        } else {
            default
        }
    }

    private fun NamedParams?.getStrokeLineCapIntOrDefault(default: Int): Int {
        return if (this != null) {
            val strokeLineCapText = if (value.contains('.')) {
                value.substringAfter(".")
            } else {
                value
            }
            StrokeCap::class.java
                .declaredFields
                .find { it.name == strokeLineCapText }
                ?.apply {
                    isAccessible = true
                }
                ?.getInt(null)
                ?: default
        } else {
            default
        }
    }

    private fun NamedParams?.getStrokeJoinIntOrDefault(default: Int): Int {
        return if (this != null) {
            val strokeJoinText = if (value.contains('.')) {
                value.substringAfter(".")
            } else {
                value
            }
            StrokeJoin::class.java
                .declaredFields
                .find { it.name == strokeJoinText }
                ?.apply {
                    isAccessible = true
                }
                ?.getInt(null)
                ?: default
        } else {
            default
        }
    }

    private fun NamedParams?.getPathFillTypeIntOrDefault(default: Int): Int {
        return if (this != null) {
            val pathFillTypeText = if (value.contains('.')) {
                value.substringAfter(".")
            } else {
                value
            }
            PathFillType::class.java
                .declaredFields
                .find { it.name == pathFillTypeText }
                ?.apply {
                    isAccessible = true
                }
                ?.getInt(null)
                ?: default
        } else {
            default
        }
    }

    private fun getPathBuilderContentOperations(text: String): List<String> {
        return text.split('\n')
            .map { it.trim() }
    }

    @Suppress("SpreadOperator")
    private fun PathBuilder.perform(pathBuilderOperation: String) {
        val pathBuilderClass = PathBuilder::class.java
        val operationString = pathBuilderOperation.substringBefore('(')
        val paramsString = pathBuilderOperation.substringAfter('(').substringBefore(')')
        pathBuilderClass.methods
            .find { it.name == operationString }
            ?.invoke(
                this,
                *getPathBuilderOperationParams(paramsString)
            )
    }

    private fun getPathBuilderOperationParams(paramsString: String): Array<Any> {
        if (paramsString.isBlank()) return emptyArray()
        return paramsString.split(',')
            .map { it.trim() }
            .map { it.toBooleanStrictOrNull() ?: it.toFloat() }
            .toTypedArray()
    }
}
