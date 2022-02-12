package by.overpass.svgtocomposeintellij

import com.android.tools.idea.ui.wizard.StudioWizardDialogBuilder
import com.android.tools.idea.wizard.model.ModelWizard
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
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
                        SvgToComposeWizardModel(
                            event.project
                                ?: throw IllegalStateException("Can't start Svg To Compose UI: the project is null"),
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
