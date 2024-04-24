package by.overpass.svgtocomposeintellij.generator.data

import br.com.devsrsouza.svg2compose.Svg2Compose
import br.com.devsrsouza.svg2compose.VectorType
import by.overpass.svgtocomposeintellij.generator.domain.SvgIconsGenerator
import by.overpass.svgtocomposeintellij.generator.domain.SvgToComposeData
import by.overpass.svgtocomposeintellij.generator.domain.SvgToComposeDataProcessor
import by.overpass.svgtocomposeintellij.generator.domain.VectorImageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataProcessingSvgIconsSvgIconsGenerator(
    private val processData: SvgToComposeDataProcessor,
) : SvgIconsGenerator {

    override suspend fun generate(data: SvgToComposeData): Result<Unit> = withContext(Dispatchers.IO) {
        val processedData = processData(data)
        runCatching {
            Svg2Compose.parse(
                applicationIconPackage = processedData.applicationIconPackage,
                accessorName = processedData.accessorName,
                outputSourceDirectory = processedData.outputDir,
                vectorsDirectory = processedData.vectorsDir,
                type = processedData.vectorImageType.asVectorType(),
                allAssetsPropertyName = processedData.allAssetsPropertyName,
            )
            Unit
        }
    }
}

private fun VectorImageType.asVectorType(): VectorType =
    when (this) {
        VectorImageType.SVG -> VectorType.SVG
        VectorImageType.DRAWABLE -> VectorType.DRAWABLE
    }
