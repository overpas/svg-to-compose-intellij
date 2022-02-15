package by.overpass.svgtocomposeintellij

import by.overpass.svgtocomposeintellij.data.SvgToComposeDataProcessor
import by.overpass.svgtocomposeintellij.data.SvgToComposeService
import by.overpass.svgtocomposeintellij.data.SvgToComposeTaskFactory
import by.overpass.svgtocomposeintellij.presentation.SvgToComposeWizardViewModel
import by.overpass.svgtocomposeintellij.ui.SvgToComposeWizardStep
import com.android.tools.idea.ui.wizard.StudioWizardDialogBuilder
import com.android.tools.idea.wizard.model.ModelWizard
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.IconLoader
import com.intellij.util.ui.JBUI
import java.io.File

private const val MESSAGE_PROJECT_NULL = "Can't start Svg2Compose UI: the project is null"

private const val MIN_WINDOW_WIDTH = 800
private const val MIN_WINDOW_HEIGHT = 600
private const val PREFERRED_WINDOW_WIDTH = 1020
private const val PREFERRED_WINDOW_HEIGHT = 680

class SvgToComposeAction : AnAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = icon
    }

    override fun actionPerformed(event: AnActionEvent) {
        StudioWizardDialogBuilder(
            ModelWizard.Builder()
                .addStep(
                    SvgToComposeWizardStep(
                        SvgToComposeWizardViewModel(
                            SvgToComposeService(
                                ProgressManager.getInstance(),
                                SvgToComposeTaskFactory(
                                    event.project
                                        ?: throw IllegalStateException(MESSAGE_PROJECT_NULL),
                                    SvgToComposeDataProcessor(),
                                )
                            ),
                            event.targetDir,
                        ),
                        "Create Compose Icons from SVG or Android vector drawables",
                    )
                )
                .build(),
            "SVG to Compose",
        )
            .setProject(event.project)
            .setMinimumSize(JBUI.size(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT))
            .setPreferredSize(JBUI.size(PREFERRED_WINDOW_WIDTH, PREFERRED_WINDOW_HEIGHT))
            .build()
            .show()
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
