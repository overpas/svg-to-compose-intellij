package by.overpass.svgtocomposeintellij.presentation.validation

class NonEqualToValueValidator<in T>(
    private val wrongTarget: T,
    private val message: String,
) : ValueValidator<T, String> {

    override fun validate(value: T): ValidationResult<String> = if (value == wrongTarget) {
        ValidationResult.Error(message)
    } else {
        ValidationResult.Ok
    }
}