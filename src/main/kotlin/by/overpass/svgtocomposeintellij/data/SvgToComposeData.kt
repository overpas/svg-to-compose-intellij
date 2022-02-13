package by.overpass.svgtocomposeintellij.data

import br.com.devsrsouza.svg2compose.VectorType
import java.io.File

data class SvgToComposeData(
    val applicationIconPackage: String,
    val accessorName: String,
    val outputDir: File,
    val vectorsDir: File,
    val vectorImageType: VectorType,
    val allAssetsPropertyName: String,
)