package by.overpass.svgtocomposeintellij.generator.domain

import java.io.File

data class SvgToComposeData(
    val applicationIconPackage: String,
    val accessorName: String,
    val outputDir: File,
    val vectorsDir: File,
    val vectorImageType: VectorImageType,
    val allAssetsPropertyName: String,
    val generateStringAccessor: Boolean,
    val generatePreview: Boolean,
)
