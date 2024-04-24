package by.overpass.svgtocomposeintellij.generator.presentation.validation

object CantBeEmptyStringValidator : ValueValidator<String, Unit> {

    override fun validate(value: String): ValidationResult<Unit> =
        if (value.isBlank()) {
            ValidationResult.Error(Unit)
        } else {
            ValidationResult.Ok
        }
}
