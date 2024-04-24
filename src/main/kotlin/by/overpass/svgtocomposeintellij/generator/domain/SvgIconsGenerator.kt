package by.overpass.svgtocomposeintellij.generator.domain

interface SvgIconsGenerator {

    suspend fun generate(data: SvgToComposeData): Result<Unit>
}
