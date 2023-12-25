package by.overpass.svgtocomposeintellij.presentation.validation

sealed class DirError {

    data object Empty : DirError()

    data object InvalidPath : DirError()

    data object NotADirectory : DirError()
}
