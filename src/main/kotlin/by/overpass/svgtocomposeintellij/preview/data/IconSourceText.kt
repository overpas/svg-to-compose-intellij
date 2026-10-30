package by.overpass.svgtocomposeintellij.preview.data

internal class IconSourceText(private val text: String) {

    private val builderPattern =
        """=\s*Builder\((?<builderParams>[\S\s]*)\)\.apply\s*\{(?<builderContent>[\S\s]*)\}\s*\n\s*\.build\(\)"""
            .toRegex()
    private val namedParamsPattern =
        """(?<paramName>\w+)[\s\n]*=[\s\n]*(?<paramValue>[\w."()]+)(,|([\s\n]*\)))""".toRegex()
    private val pathOperationPattern = """path\s*(?<pathParams>\([^{}]*)?\{(?<pathContent>[^{}]*)\}""".toRegex()

    fun iconName(): String =
        imageVectorDeclarationPattern.find(text)?.run { groupValues[2] } ?: error("Failed to parse icon name")

    fun builderParams(): NamedParams =
        namedParams("${builderGroup("builderParams")})")

    fun pathOperations(): List<PathOperation> {
        val builderContent = builderGroup("builderContent").trim { it.isWhitespace() || it == '\n' }
        return pathOperationPattern.findAll(builderContent)
            .mapNotNull { result -> result.toPathOperation() }
            .toList()
    }

    private fun builderGroup(name: String): String =
        builderPattern.find(text)?.run { groups[name]?.value } ?: error("Failed to parse icon")

    private fun namedParams(paramsText: String): NamedParams =
        NamedParams(
            namedParamsPattern.findAll(paramsText)
                .map { result -> result.groupValues[1] to result.groupValues[2] }
                .toList(),
        )

    private fun MatchResult.toPathOperation(): PathOperation? {
        val pathContent = groups["pathContent"]?.run { value.trim { it.isWhitespace() || it == '\n' } }
            ?: return null
        val pathParams = groups["pathParams"]?.run { value.trimStart { it.isWhitespace() || it == '(' || it == '\n' } }
        return PathOperation(
            params = pathParams?.let(::namedParams) ?: NamedParams(emptyList()),
            operations = pathContent.lines().map { line -> line.trim() },
        )
    }
}
