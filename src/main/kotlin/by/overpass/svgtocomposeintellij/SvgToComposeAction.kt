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
                                        ?: throw IllegalStateException("Can't start Svg2Compose UI: the project is null"),
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
            .setMinimumSize(JBUI.size(800, 600))
            .setPreferredSize(JBUI.size(1020, 680))
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
