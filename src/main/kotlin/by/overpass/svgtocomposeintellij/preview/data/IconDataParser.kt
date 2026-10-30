package by.overpass.svgtocomposeintellij.preview.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface IconDataParser {

    suspend fun parse(): Result<IconData>
}

class KotlinFileIconDataParser(
    private val readSource: () -> String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : IconDataParser {

    override suspend fun parse(): Result<IconData> =
        withContext(dispatcher) {
            runCatching {
                val source = IconSourceText(readSource())
                val name = source.iconName()
                val builder = createImageVectorBuilder(source.builderParams())
                source.pathOperations().forEach(builder::addPath)
                IconData(
                    name = name,
                    imageVector = builder.build(),
                )
            }
        }.onFailure { throwable ->
            throwable.printStackTrace()
        }
}
