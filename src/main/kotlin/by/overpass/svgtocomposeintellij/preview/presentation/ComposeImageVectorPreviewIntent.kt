package by.overpass.svgtocomposeintellij.preview.presentation

sealed interface ComposeImageVectorPreviewIntent {

    data object ParseFile : ComposeImageVectorPreviewIntent
}
