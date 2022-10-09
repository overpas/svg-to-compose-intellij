package by.overpass.svgtocomposeintellij.presentation.validation

class CantBeEmptyStringValidator(
    private val propertyName: String,
) : ValueValidator<String?, String> {

    override fun validate(value: String?): ValidationResult<String> =
        if (value.isNullOrBlank()) {
            ValidationResult.Error("$propertyName can't be empty")
        } else {
            ValidationResult.Ok
        }
}