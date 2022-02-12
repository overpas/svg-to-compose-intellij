package by.overpass.svgtocomposeintellij

import br.com.devsrsouza.svg2compose.Svg2Compose
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project

class SvgToComposeTask(
    project: Project,
    private val model: SvgToComposeWizardModel,
) : Task.Backgroundable(
    project,
    "Generating compose icons...",
    false,
) {

    override fun run(indicator: ProgressIndicator) {
        Svg2Compose.parse(
            applicationIconPackage = model.applicationIconPackage.get(),
            accessorName = model.accessorName.get(),
            outputSourceDirectory = model.outputDir.get(),
            vectorsDirectory = model.vectorsDir.get(),
            type = model.vectorImageType.get(),
            allAssetsPropertyName = model.allAssetsPropertyName.get(),
        )
    }

    override fun shouldStartInBackground(): Boolean = true
}