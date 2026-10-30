package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.ProjectScopeProviderService
import by.overpass.svgtocomposeintellij.generator.presentation.Finished
import by.overpass.svgtocomposeintellij.generator.presentation.SvgToComposeState
import by.overpass.svgtocomposeintellij.generator.presentation.SvgToComposeViewModel
import by.overpass.svgtocomposeintellij.generator.presentation.isValid
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.toComposeColor
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.JComponent

private const val DEFAULT_DIALOG_CONTENT_WIDTH = 820
private const val DEFAULT_DIALOG_CONTENT_HEIGHT = 300
private const val SWING_COMPOSITING_PROPERTY = "compose.swing.render.on.graphics"

class SvgToComposeDialog(
    private val project: Project,
    private val viewModel: SvgToComposeViewModel,
) : DialogWrapper(project) {

    private val generateAction = GenerateAction(Bundle.message("generator_button_confirm"))

    init {
        init()
        title = Bundle.message("generator_title")
        processState(viewModel.state.value)
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

    override fun createCenterPanel(): JComponent {
        System.setProperty(SWING_COMPOSITING_PROPERTY, true.toString())
        return JewelComposePanel {
            val bgColor by remember(JBColor.PanelBackground.rgb) {
                mutableStateOf(JBColor.PanelBackground.toComposeColor())
            }
            LaunchedEffect(viewModel) {
                viewModel.state.collect(::processState)
            }
            SvgToComposePlugin(
                viewModel = viewModel,
                project = project,
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor),
            )
        }.apply {
            preferredSize = JBUI.size(
                Dimension(
                    DEFAULT_DIALOG_CONTENT_WIDTH,
                    DEFAULT_DIALOG_CONTENT_HEIGHT,
                ),
            )
        }
    }

    override fun getOKAction(): Action =
        generateAction

    private inner class GenerateAction(name: String) : DialogWrapperAction(name) {

        override fun doAction(e: ActionEvent?) {
            project.service<ProjectScopeProviderService>().launch {
                viewModel.onGenerate()
            }
        }
    }
}
