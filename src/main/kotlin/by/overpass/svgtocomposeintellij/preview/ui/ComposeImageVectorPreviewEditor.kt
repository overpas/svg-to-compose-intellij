package by.overpass.svgtocomposeintellij.preview.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.preview.presentation.ComposeImageVectorPreviewIntent
import by.overpass.svgtocomposeintellij.preview.presentation.ComposeImageVectorPreviewViewModel
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import java.beans.PropertyChangeListener
import javax.swing.JComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.foundation.ExperimentalJewelApi

private const val DEFAULT_WINDOW_DIMENSION = 400

class ComposeImageVectorPreviewEditor(
    private val viewModel: ComposeImageVectorPreviewViewModel,
    private val coroutineScope: CoroutineScope,
) : FileEditor, UserDataHolderBase() {

    @OptIn(ExperimentalJewelApi::class)
    private val component = JewelComposePanel {
        SwingBridgeTheme {
            ComposeImageVectorPreview(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }.apply {
        // The size doesn't seem to matter, it will be expanded
        preferredSize = JBUI.size(Dimension(DEFAULT_WINDOW_DIMENSION, DEFAULT_WINDOW_DIMENSION))
    }

    init {
        viewModel.dispatch(ComposeImageVectorPreviewIntent.ParseFile)
    }

    override fun dispose() {
        coroutineScope.cancel()
    }

    override fun getComponent(): JComponent = component

    override fun getPreferredFocusedComponent(): JComponent = component

    override fun getName(): String {
        return Bundle.message("preview_editor_title")
    }

    override fun setState(state: FileEditorState) {
        // do nothing
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
        // do nothing
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
        // do nothing
    }
}
