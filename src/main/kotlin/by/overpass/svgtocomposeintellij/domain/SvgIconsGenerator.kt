package by.overpass.svgtocomposeintellij.domain

interface SvgIconsGenerator {

    suspend fun generate(data: SvgToComposeData): Result<Unit>
}
