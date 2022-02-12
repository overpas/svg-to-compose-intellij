package by.overpass.svgtocomposeintellij

import br.com.devsrsouza.svg2compose.VectorType
import com.android.tools.idea.observable.core.ObjectValueProperty
import com.android.tools.idea.observable.core.StringValueProperty
import com.android.tools.idea.wizard.model.WizardModel
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import java.io.File

class SvgToComposeWizardModel(private val project: Project, targetDir: File?) : WizardModel() {

    val stubFile = File("")

    val applicationIconPackage = StringValueProperty("icons")
    val accessorName = StringValueProperty("MyIconPack")
    val outputDir = ObjectValueProperty<File>(targetDir ?: stubFile)
    val vectorsDir = ObjectValueProperty<File>(stubFile)
    val vectorImageType = ObjectValueProperty<VectorType>(VectorType.SVG)
    val allAssetsPropertyName = StringValueProperty("AllIcons")

    override fun handleFinished() {
        ProgressManager.getInstance().run(SvgToComposeTask(project, this))
    }
}