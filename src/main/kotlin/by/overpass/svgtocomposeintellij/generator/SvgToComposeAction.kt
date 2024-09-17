package by.overpass.svgtocomposeintellij.generator

import by.overpass.svgtocomposeintellij.generator.data.DataProcessingSvgIconsSvgIconsGenerator
import by.overpass.svgtocomposeintellij.generator.domain.SvgToComposeDataProcessor
import by.overpass.svgtocomposeintellij.generator.domain.VectorImageTypeDetector
import by.overpass.svgtocomposeintellij.generator.presentation.SvgToComposeViewModelImpl
import by.overpass.svgtocomposeintellij.generator.presentation.validation.CantBeEmptyStringValidator
import by.overpass.svgtocomposeintellij.generator.presentation.validation.ProperDirValidator
import by.overpass.svgtocomposeintellij.generator.ui.SvgToComposeDialog
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.util.IconLoader
import java.io.File
import kotlinx.coroutines.Dispatchers

private const val MESSAGE_PROJECT_NULL = "Can't start Svg2Compose UI: the project is null"

class SvgToComposeAction : AnAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = icon
    }

    override fun actionPerformed(event: AnActionEvent) {
        SvgToComposeDialog(
            event.project ?: throw IllegalStateException(MESSAGE_PROJECT_NULL),
            SvgToComposeViewModelImpl(
                targetDir = event.targetDir ?: File(""),
                svgIconsGenerator = DataProcessingSvgIconsSvgIconsGenerator(SvgToComposeDataProcessor),
                vectorImageTypeDetector = VectorImageTypeDetector,
                nonStringEmptyValidator = CantBeEmptyStringValidator,
                directoryValidator = ProperDirValidator,
                dispatcher = Dispatchers.Default,
            ),
        ).show()
    }

    override fun getActionUpdateThread(): ActionUpdateThread =
        ActionUpdateThread.BGT

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
