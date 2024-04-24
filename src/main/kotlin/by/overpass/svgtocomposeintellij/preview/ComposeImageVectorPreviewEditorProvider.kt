package by.overpass.svgtocomposeintellij.preview

import by.overpass.svgtocomposeintellij.preview.data.KotlinFileIconDataParser
import by.overpass.svgtocomposeintellij.preview.data.imageVectorDeclarationPattern
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

class ComposeImageVectorPreviewEditorProvider : FileEditorProvider, DumbAware {

    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file.containsImageVector()
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        val textEditor = TextEditorProvider.getInstance().createEditor(project, file) as TextEditor
        return TextEditorWithPreview(
            textEditor,
            ComposeImageVectorPreviewEditor(
                KotlinFileIconDataParser(
                    file.toNioPath()
                        .toFile(),
                )
            ),
            "ComposeImageVectorEditor",
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
