package by.overpass.svgtocomposeintellij.preview

import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.initMainDispatcherChecker
import by.overpass.svgtocomposeintellij.preview.data.KotlinFileIconDataParser
import by.overpass.svgtocomposeintellij.preview.data.containsImageVector
import by.overpass.svgtocomposeintellij.preview.data.readContent
import by.overpass.svgtocomposeintellij.preview.presentation.ComposeImageVectorPreviewViewModelImpl
import by.overpass.svgtocomposeintellij.preview.ui.ComposeImageVectorPreviewEditor
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.TextEditorWithPreview
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class ComposeImageVectorPreviewEditorProvider :
    FileEditorProvider,
    DumbAware {

    override fun accept(
        project: Project,
        file: VirtualFile,
    ): Boolean =
        file.containsImageVector()

    override fun createEditor(
        project: Project,
        file: VirtualFile,
    ): FileEditor {
        initMainDispatcherChecker()
        val textEditor = TextEditor::class.java.cast(TextEditorProvider.getInstance().createEditor(project, file))
        return createTextEditorWithPreview(file, textEditor)
    }

    private fun createTextEditorWithPreview(
        file: VirtualFile,
        textEditor: TextEditor,
    ): TextEditorWithPreview {
        val iconDataParser = KotlinFileIconDataParser(readSource = file::readContent)
        val viewModel = ComposeImageVectorPreviewViewModelImpl(iconDataParser = iconDataParser)
        val composeImageVectorPreviewEditor = ComposeImageVectorPreviewEditor(viewModel = viewModel)
        val name = Bundle.message("preview_editor_title")
        return TextEditorWithPreview(
            myEditor = textEditor,
            myPreview = composeImageVectorPreviewEditor,
            name = name,
            defaultLayout = TextEditorWithPreview.Layout.SHOW_EDITOR_AND_PREVIEW,
            isVerticalSplit = false,
            layout = null,
        )
    }

    override fun getEditorTypeId(): String =
        "compose-image-vector-preview-editor"

    override fun getPolicy(): FileEditorPolicy =
        FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
}
