package by.overpass.svgtocomposeintellij.data

import java.io.File

interface SvgToComposeDataProcessor {
    operator fun invoke(data: SvgToComposeData): SvgToComposeData

    companion object {
        operator fun invoke(): SvgToComposeDataProcessor = SvgToComposeDataProcessorImpl()
    }
}

class SvgToComposeDataProcessorImpl : SvgToComposeDataProcessor {

    private val pathRegex = ".*[/\\\\](kotlin|java)[/\\\\](.*)".toRegex()

    override operator fun invoke(data: SvgToComposeData): SvgToComposeData {
        val initialPath = data.outputDir.path
        val initialPackage = data.applicationIconPackage
        val pkg = pathRegex.find(initialPath)?.groupValues?.get(2)
        return if (pkg.isNullOrEmpty()) {
            data
        } else {
            val newPath = initialPath.removeSuffix(pkg)
            val newPackage = "${pkg.replace("[/\\\\]".toRegex(), ".")}.$initialPackage"
            val outputDir = File(newPath)
            data.copy(outputDir = outputDir, applicationIconPackage = newPackage)
        }
    }
}