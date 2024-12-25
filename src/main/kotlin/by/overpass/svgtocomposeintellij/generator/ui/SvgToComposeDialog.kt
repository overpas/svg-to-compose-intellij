package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.generator.presentation.Finished
import by.overpass.svgtocomposeintellij.generator.presentation.SvgToComposeState
import by.overpass.svgtocomposeintellij.generator.presentation.SvgToComposeViewModel
import by.overpass.svgtocomposeintellij.generator.presentation.isValid
import com.intellij.openapi.application.EDT
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.toComposeColor
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.JComponent

private const val DEFAULT_DIALOG_CONTENT_WIDTH = 820
private const val DEFAULT_DIALOG_CONTENT_HEIGHT = 300

class SvgToComposeDialog(
    project: Project,
    private val viewModel: SvgToComposeViewModel,
) : DialogWrapper(project) {

    private val coroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.EDT + CoroutineName(this::class.java.simpleName),
    )

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

    @OptIn(ExperimentalJewelApi::class)
    override fun createCenterPanel(): JComponent {
        enableNewSwingCompositing()
        return JewelComposePanel {
            val bgColor by remember(JBColor.PanelBackground.rgb) {
                mutableStateOf(JBColor.PanelBackground.toComposeColor())
            }
//            Box(
//              modifier = Modifier.fillMaxSize()
//                  .background(bgColor),
//            ) {
//                Text(
//                    text = "Text",
//                    color = Color.Red,
//                    modifier = Modifier.align(Alignment.Center),
//                )
//            }
            SvgToComposePlugin(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
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
