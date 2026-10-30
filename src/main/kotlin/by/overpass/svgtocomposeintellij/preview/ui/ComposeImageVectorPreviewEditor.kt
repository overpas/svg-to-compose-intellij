package by.overpass.svgtocomposeintellij.preview.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.preview.presentation.ComposeImageVectorPreviewViewModel
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.util.ui.JBUI
import org.jetbrains.jewel.bridge.JewelComposePanel
import java.awt.Dimension
import java.beans.PropertyChangeListener
import javax.swing.JComponent

private const val DEFAULT_WINDOW_DIMENSION = 400

class ComposeImageVectorPreviewEditor(viewModel: ComposeImageVectorPreviewViewModel) :
    UserDataHolderBase(),
    FileEditor {

    private val component = JewelComposePanel {
        ComposeImageVectorPreview(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize(),
        )
    }.apply {
        // The size doesn't seem to matter, it will be expanded
        preferredSize = JBUI.size(Dimension(DEFAULT_WINDOW_DIMENSION, DEFAULT_WINDOW_DIMENSION))
    }

    override fun dispose() {
        // do nothing
    }

    override fun getComponent(): JComponent =
        component

    override fun getPreferredFocusedComponent(): JComponent =
        component

    override fun getName(): String =
        Bundle.message("preview_editor_title")

    override fun setState(state: FileEditorState) {
        // do nothing
    }

    override fun isModified(): Boolean =
        false

    override fun isValid(): Boolean =
        true

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
        // do nothing
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
        // do nothing
    }
}
