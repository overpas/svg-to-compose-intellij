package by.overpass.svgtocomposeintellij.generator.domain

import java.io.File

interface SvgToComposeDataProcessor {

    operator fun invoke(data: SvgToComposeData): SvgToComposeData

    companion object : SvgToComposeDataProcessor {

        private val pathRegex = ".*[/\\\\](kotlin|java)[/\\\\](.*)".toRegex()

        override operator fun invoke(data: SvgToComposeData): SvgToComposeData {
            val initialPath = data.outputDir.path
            val pkg = pathRegex.find(initialPath)?.groupValues?.get(2)
            return if (pkg.isNullOrEmpty()) {
                data
            } else {
                val newPath = initialPath.removeSuffix(pkg)
                val newPackage = pkg.replace("[/\\\\]".toRegex(), ".")
                val outputDir = File(newPath)
                data.copy(outputDir = outputDir, applicationIconPackage = newPackage)
            }
        }
    }
}
