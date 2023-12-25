package by.overpass.svgtocomposeintellij.ui

import by.overpass.svgtocomposeintellij.presentation.SvgToComposeViewModel
import by.overpass.svgtocomposeintellij.presentation.isValid
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.JComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private const val DEFAULT_DIALOG_CONTENT_WIDTH = 820
private const val DEFAULT_DIALOG_CONTENT_HEIGHT = 300

class SvgToComposeDialog(
    project: Project,
    private val viewModel: SvgToComposeViewModel,
) : DialogWrapper(project, false) {

    private val coroutineScope = CoroutineScope(SupervisorJob())

    private val action = GenerateAction("Generate")

    init {
        init()
        title = "Generate Compose Icons From SVG Or Android Vector Drawables"
        viewModel.state
            .onEach { model ->
                action.isEnabled = model.isValid
            }
            .launchIn(coroutineScope)
    }

    override fun createCenterPanel(): JComponent =
        svgToComposePluginPanel(viewModel).apply {
            preferredSize = JBUI.size(Dimension(DEFAULT_DIALOG_CONTENT_WIDTH, DEFAULT_DIALOG_CONTENT_HEIGHT))
        }

    override fun getOKAction(): Action = action

    override fun dispose() {
        super.dispose()
        coroutineScope.cancel()
    }

    private inner class GenerateAction(name: String) : DialogWrapperAction(name) {

        override fun doAction(e: ActionEvent?) {
            viewModel.generate()
            close(OK_EXIT_CODE, true)
        }
    }
}
