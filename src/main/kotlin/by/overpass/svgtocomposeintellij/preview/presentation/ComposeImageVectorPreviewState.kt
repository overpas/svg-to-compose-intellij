package by.overpass.svgtocomposeintellij.preview.presentation

import by.overpass.svgtocomposeintellij.preview.data.IconData

sealed class ComposeImageVectorPreviewState {

    data object Rendering : ComposeImageVectorPreviewState()

    data object Error : ComposeImageVectorPreviewState()

    data class Icon(
        val data: IconData,
    ): ComposeImageVectorPreviewState()
}
