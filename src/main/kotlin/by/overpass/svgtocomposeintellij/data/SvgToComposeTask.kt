package by.overpass.svgtocomposeintellij.data

import br.com.devsrsouza.svg2compose.Svg2Compose
import by.overpass.svgtocomposeintellij.domain.SvgToComposeData
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project

class SvgToComposeTask(
    project: Project,
    private val data: SvgToComposeData,
) : Task.Backgroundable(
    project,
    "Generating compose icons...",
    false,
) {

    override fun run(indicator: ProgressIndicator) {
        Svg2Compose.parse(
            applicationIconPackage = data.applicationIconPackage,
            accessorName = data.accessorName,
            outputSourceDirectory = data.outputDir,
            vectorsDirectory = data.vectorsDir,
            type = data.vectorImageType,
            allAssetsPropertyName = data.allAssetsPropertyName,
        )
    }

    override fun shouldStartInBackground(): Boolean = true
}