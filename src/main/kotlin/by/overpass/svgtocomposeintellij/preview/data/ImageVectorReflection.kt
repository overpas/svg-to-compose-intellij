package by.overpass.svgtocomposeintellij.preview.data

import androidx.compose.ui.graphics.vector.DefaultGroupName
import androidx.compose.ui.graphics.vector.DefaultPathName
import androidx.compose.ui.graphics.vector.DefaultStrokeLineMiter
import androidx.compose.ui.graphics.vector.DefaultStrokeLineWidth
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import java.lang.invoke.MethodHandles
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy

private const val BUILDER_PARAMETER_COUNT = 8
private const val DEFAULT_FILL_ALPHA = 0.5f
private const val DEFAULT_STROKE_ALPHA = 0.5f
private const val FUNCTION_CLASS_NAME = "kotlin.jvm.functions.Function1"

internal fun createImageVectorBuilder(params: NamedParams): ImageVector.Builder {
    val builderClass = ImageVector.Builder::class.java
    val builderConstructor = builderClass.declaredConstructors
        .first { constructor ->
            constructor.parameterCount == BUILDER_PARAMETER_COUNT && Modifier.isPrivate(constructor.modifiers)
        }
        .apply {
            isAccessible = true
        }
    return builderClass.cast(
        builderConstructor.newInstance(
            params.string("name", DefaultGroupName),
            params.float("defaultWidth"),
            params.float("defaultHeight"),
            params.float("viewportWidth"),
            params.float("viewportHeight"),
            params.color("tintColor"),
            params.blendMode("tintBlendMode"),
            params.autoMirror("autoMirror"),
        ),
    )
}

internal fun ImageVector.Builder.addPath(pathOperation: PathOperation) {
    val params = pathOperation.params
    val pathMethod = Class.forName("androidx.compose.ui.graphics.vector.ImageVectorKt")
        .declaredMethods
        .find { method ->
            "path" in method.name && method.parameters.last().type.name == FUNCTION_CLASS_NAME
        }
        ?.apply {
            isAccessible = true
        }
    val functionClass = Class.forName(FUNCTION_CLASS_NAME)
    val pathBuilderProxy = Proxy.newProxyInstance(
        functionClass.classLoader,
        arrayOf<Class<*>>(functionClass),
    ) { _, _, args ->
        val pathBuilder = PathBuilder::class.java.cast(args[0])
        pathOperation.operations.forEach(pathBuilder::perform)
    }
    pathMethod?.invoke(
        null,
        this,
        params.string("name", DefaultPathName),
        params.brush("fill"),
        params.floatOrDefault("fillAlpha", DEFAULT_FILL_ALPHA),
        params.brush("stroke"),
        params.floatOrDefault("strokeAlpha", DEFAULT_STROKE_ALPHA),
        params.floatOrDefault("strokeLineWidth", DefaultStrokeLineWidth),
        params.strokeCap("strokeLineCap"),
        params.strokeJoin("strokeLineJoin"),
        params.floatOrDefault("strokeLineMiter", DefaultStrokeLineMiter),
        params.pathFillType("pathFillType"),
        pathBuilderProxy,
    )
}

private fun PathBuilder.perform(pathBuilderOperation: String) {
    val operationName = pathBuilderOperation.substringBefore('(')
    val paramsText = pathBuilderOperation.substringAfter('(').substringBefore(')')
    PathBuilder::class.java.methods
        .find { method -> method.name == operationName }
        ?.let { method ->
            MethodHandles.lookup()
                .unreflect(method)
                .invokeWithArguments(listOf(this) + operationParams(paramsText))
        }
}

private fun operationParams(paramsText: String): List<Any> =
    if (paramsText.isBlank()) {
        emptyList()
    } else {
        paramsText.split(',')
            .map { param -> param.trim() }
            .map { param -> param.toBooleanStrictOrNull() ?: param.toFloat() }
    }
