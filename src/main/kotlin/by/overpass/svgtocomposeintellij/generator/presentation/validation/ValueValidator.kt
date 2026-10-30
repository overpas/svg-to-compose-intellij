package by.overpass.svgtocomposeintellij.generator.presentation.validation

interface ValueValidator<in T, R> {

    fun validate(value: T): ValidationResult<R>
}
