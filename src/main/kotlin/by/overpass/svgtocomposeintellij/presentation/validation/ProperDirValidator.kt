package by.overpass.svgtocomposeintellij.presentation.validation

import java.io.File

class ProperDirValidator(
    private val stubFile: File,
) : ValueValidator<File, ProperDirValidator.Error> {

    override fun validate(value: File): ValidationResult<Error> =
        when {
            value == stubFile || value.path.isBlank() -> ValidationResult.Error(Error.Empty)
            !value.exists() -> ValidationResult.Error(Error.InvalidPath)
            !value.isDirectory -> ValidationResult.Error(Error.NotADirectory)
            else -> ValidationResult.Ok
        }

    sealed class Error {

        object Empty : Error()

        object InvalidPath : Error()

        object NotADirectory : Error()
    }
}