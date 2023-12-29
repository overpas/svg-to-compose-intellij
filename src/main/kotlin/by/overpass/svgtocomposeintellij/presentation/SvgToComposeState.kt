package by.overpass.svgtocomposeintellij.presentation

import by.overpass.svgtocomposeintellij.domain.VectorImageType
import by.overpass.svgtocomposeintellij.presentation.validation.DirError
import by.overpass.svgtocomposeintellij.presentation.validation.Validatable

sealed interface SvgToComposeState
data class DataInput(
    val accessorName: Validatable<String, Unit> = Validatable(value = "MyIconPack"),
    val outputDir: Validatable<String, DirError> = Validatable(value = "", isValid = false),
    val vectorImagesDir: Validatable<String, DirError> = Validatable(value = "", isValid = false),
    val vectorImageType: VectorImageType = VectorImageType.SVG,
    val allAssetsPropertyName: Validatable<String, Unit> = Validatable(value = "AllIcons"),
    val isInProgress: Boolean = false,
) : SvgToComposeState

data class Error(
    val throwable: Throwable,
) : SvgToComposeState

data object Finished : SvgToComposeState

val SvgToComposeState.isValid: Boolean
    get() = if (this is DataInput) {
        accessorName.isValid && outputDir.isValid && vectorImagesDir.isValid && allAssetsPropertyName.isValid
    } else {
        false
    }