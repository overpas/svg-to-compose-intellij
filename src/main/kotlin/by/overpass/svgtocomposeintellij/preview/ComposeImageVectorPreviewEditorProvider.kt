package by.overpass.svgtocomposeintellij.preview

import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.initializeComposeMainDispatcherChecker
import by.overpass.svgtocomposeintellij.preview.data.KotlinFileIconDataParser
import by.overpass.svgtocomposeintellij.preview.data.asInputStream
import by.overpass.svgtocomposeintellij.preview.data.imageVectorDeclarationPattern
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.skiko.MainUIDispatcher
import java.io.InputStream

class ComposeImageVectorPreviewEditorProvider : FileEditorProvider, DumbAware {

    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file.containsImageVector()
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        initializeComposeMainDispatcherChecker()
        val textEditor = TextEditorProvider.getInstance().createEditor(project, file) as TextEditor
        val inputStream = file.asInputStream()
        return createTextEditorWithPreview(inputStream, textEditor)
    }

    private fun createTextEditorWithPreview(
        inputStream: InputStream,
        textEditor: TextEditor
    ): TextEditorWithPreview {
        val coroutineScope = CoroutineScope(MainUIDispatcher + SupervisorJob())
        val iconDataParser = KotlinFileIconDataParser(inputStream)
        val viewModel = ComposeImageVectorPreviewViewModelImpl(
            coroutineScope = coroutineScope,
            iconDataParser = iconDataParser,
        )
        val composeImageVectorPreviewEditor = ComposeImageVectorPreviewEditor(
            viewModel = viewModel,
            coroutineScope = coroutineScope,
        )
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

    override fun getEditorTypeId(): String = "compose-image-vector-preview-editor"

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR

    private fun VirtualFile.containsImageVector(): Boolean {
        return inputStream
            .bufferedReader()
            .useLines { lines ->
                lines.find { line ->
                    imageVectorDeclarationPattern.containsMatchIn(line)
                } != null
            }
    }
}
