package by.overpass.svgtocomposeintellij.generator.domain

import java.io.File

interface SvgToComposeDataProcessor {

    operator fun invoke(data: SvgToComposeData): SvgToComposeData
}

class SvgToComposeDataProcessorImpl(private val sourceRootResolver: SourceRootResolver) : SvgToComposeDataProcessor {

    private val conventionalSourceRootRegex = """(.*[/\\]src[/\\](?:[^/\\]+[/\\])??(?:kotlin|java))[/\\].+""".toRegex()

    override operator fun invoke(data: SvgToComposeData): SvgToComposeData {
        val outputDir = data.outputDir
        val sourceRoot = sourceRootResolver.resolve(outputDir)?.takeIf { outputDir.startsWith(it) }
            ?: conventionalSourceRoot(outputDir)
            ?: return data
        val newPackage = outputDir.relativeTo(sourceRoot).invariantSeparatorsPath.replace('/', '.')
        return if (newPackage.isEmpty()) {
            data
        } else {
            data.copy(outputDir = sourceRoot, applicationIconPackage = newPackage)
        }
    }

    private fun conventionalSourceRoot(dir: File): File? =
        conventionalSourceRootRegex.matchEntire(dir.path)?.run { File(groupValues[1]) }
}
