package by.overpass.svgtocomposeintellij

import by.overpass.svgtocomposeintellij.data.DataProcessingSvgIconsSvgIconsGenerator
import by.overpass.svgtocomposeintellij.domain.SvgToComposeDataProcessor
import by.overpass.svgtocomposeintellij.domain.VectorImageTypeDetector
import by.overpass.svgtocomposeintellij.presentation.SvgToComposeViewModelImpl
import by.overpass.svgtocomposeintellij.presentation.validation.CantBeEmptyStringValidator
import by.overpass.svgtocomposeintellij.presentation.validation.ProperDirValidator
import by.overpass.svgtocomposeintellij.ui.SvgToComposeDialog
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
