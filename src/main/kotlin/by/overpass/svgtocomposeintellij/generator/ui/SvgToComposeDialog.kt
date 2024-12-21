package by.overpass.svgtocomposeintellij.generator.ui

import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.generator.presentation.Finished
import by.overpass.svgtocomposeintellij.generator.presentation.SvgToComposeState
import by.overpass.svgtocomposeintellij.generator.presentation.SvgToComposeViewModel
import by.overpass.svgtocomposeintellij.generator.presentation.isValid
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.FieldPanel
import com.intellij.ui.components.Panel
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
import org.jetbrains.skiko.MainUIDispatcher

private const val DEFAULT_DIALOG_CONTENT_WIDTH = 820
private const val DEFAULT_DIALOG_CONTENT_HEIGHT = 300

class SvgToComposeDialog(
    project: Project,
    private val viewModel: SvgToComposeViewModel,
) : DialogWrapper(project, false) {

    private val coroutineScope = CoroutineScope(MainUIDispatcher + SupervisorJob())

    private val generateAction = GenerateAction(Bundle.message("generator_button_confirm"))

    init {
        init()
        title = Bundle.message("generator_title")
        viewModel.state
            .onEach(::processState)
            .launchIn(coroutineScope)
    }

    private fun processState(state: SvgToComposeState) {
        generateAction.isEnabled = state.isValid
        if (state is Finished) {
            handleFinished()
        }
    }

    private fun handleFinished() {
        close(OK_EXIT_CODE, true)
    }

    override fun createCenterPanel(): JComponent =
        svgToComposePluginPanel(viewModel).apply {
            preferredSize = JBUI.size(Dimension(DEFAULT_DIALOG_CONTENT_WIDTH, DEFAULT_DIALOG_CONTENT_HEIGHT))
        }
//        FieldPanel()

    override fun getOKAction(): Action = generateAction

    override fun dispose() {
        super.dispose()
        coroutineScope.cancel()
        viewModel.onCleared()
    }

    private inner class GenerateAction(name: String) : DialogWrapperAction(name) {

        override fun doAction(e: ActionEvent?) {
            viewModel.generate()
        }
    }
}
