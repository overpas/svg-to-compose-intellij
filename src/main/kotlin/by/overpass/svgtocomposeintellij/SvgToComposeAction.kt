package by.overpass.svgtocomposeintellij

import by.overpass.svgtocomposeintellij.data.SvgToComposeTaskFactoryImpl
import by.overpass.svgtocomposeintellij.domain.SvgToComposeDataProcessor
import by.overpass.svgtocomposeintellij.domain.SvgToComposeService
import by.overpass.svgtocomposeintellij.presentation.SvgToComposeWizardViewModel
import by.overpass.svgtocomposeintellij.ui.SvgToComposeDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.IconLoader
import java.io.File

private const val MESSAGE_PROJECT_NULL = "Can't start Svg2Compose UI: the project is null"

class SvgToComposeAction : AnAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = icon
    }

    override fun actionPerformed(event: AnActionEvent) {
        SvgToComposeDialog(
            event.project ?: throw IllegalStateException(MESSAGE_PROJECT_NULL),
            SvgToComposeWizardViewModel(
                SvgToComposeService(
                    ProgressManager.getInstance(),
                    SvgToComposeTaskFactoryImpl(
                        event.project
                            ?: throw IllegalStateException(MESSAGE_PROJECT_NULL),
                        SvgToComposeDataProcessor(),
                    )
                ),
                event.targetDir,
            ),
        ).show()
    }

    private val AnActionEvent.targetDir: File?
        get() {
            val dataContext = dataContext
            var targetDirectory = CommonDataKeys.VIRTUAL_FILE.getData(dataContext) ?: return null
            // If the user selected a simulated folder entry (eg "Manifests"), there will be no target directory
            if (!targetDirectory.isDirectory) {
                targetDirectory = targetDirectory.parent
            }
            return File(targetDirectory.path)
        }

    companion object {
        val icon = IconLoader.getIcon("/icons/ic_compose.png", SvgToComposeAction::class.java)
    }
}
