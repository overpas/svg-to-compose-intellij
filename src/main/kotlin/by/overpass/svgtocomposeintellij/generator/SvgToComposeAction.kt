package by.overpass.svgtocomposeintellij.generator

import by.overpass.svgtocomposeintellij.ProjectScopeProviderService
import by.overpass.svgtocomposeintellij.generator.data.DataProcessingSvgIconsSvgIconsGenerator
import by.overpass.svgtocomposeintellij.generator.domain.SvgToComposeDataProcessor
import by.overpass.svgtocomposeintellij.generator.domain.VectorImageTypeDetector
import by.overpass.svgtocomposeintellij.generator.presentation.SvgToComposeViewModelImpl
import by.overpass.svgtocomposeintellij.generator.presentation.validation.CantBeEmptyStringValidator
import by.overpass.svgtocomposeintellij.generator.presentation.validation.ProperDirValidator
import by.overpass.svgtocomposeintellij.generator.ui.SvgToComposeDialog
import by.overpass.svgtocomposeintellij.initMainDispatcherChecker
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.util.IconLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SvgToComposeAction : DumbAwareAction() {

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

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = icon
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = checkNotNull(event.project) { "Project not available" }
        val targetDir = event.targetDir ?: File("")
        initMainDispatcherChecker()
        project.service<ProjectScopeProviderService>().launch {
            withContext(Dispatchers.EDT) {
                SvgToComposeDialog(
                    project,
                    SvgToComposeViewModelImpl(
                        targetDir = targetDir,
                        svgIconsGenerator = DataProcessingSvgIconsSvgIconsGenerator(
                            SvgToComposeDataProcessor,
                        ),
                        vectorImageTypeDetector = VectorImageTypeDetector,
                        nonStringEmptyValidator = CantBeEmptyStringValidator,
                        directoryValidator = ProperDirValidator,
                    ),
                ).showAndGet()
            }
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread =
        ActionUpdateThread.EDT

    companion object {
        val icon = IconLoader.getIcon("/icons/ic_compose.png", SvgToComposeAction::class.java)
    }
}
