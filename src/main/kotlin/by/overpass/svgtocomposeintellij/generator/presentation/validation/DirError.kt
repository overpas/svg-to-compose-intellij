package by.overpass.svgtocomposeintellij.generator.presentation.validation

sealed interface DirError {

    data object Empty : DirError

    data object InvalidPath : DirError

    data object NotADirectory : DirError
}
