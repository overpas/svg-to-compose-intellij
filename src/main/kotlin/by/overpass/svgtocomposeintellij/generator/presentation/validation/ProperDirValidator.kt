package by.overpass.svgtocomposeintellij.generator.presentation.validation

import java.io.File

object ProperDirValidator : ValueValidator<String, DirError> {

    override fun validate(value: String): ValidationResult<DirError> {
        val file = File(value)
        return when {
            file.path.isBlank() -> ValidationResult.Error(DirError.Empty)
            !file.exists() -> ValidationResult.Error(DirError.InvalidPath)
            !file.isDirectory -> ValidationResult.Error(DirError.NotADirectory)
            else -> ValidationResult.Ok
        }
    }
}
