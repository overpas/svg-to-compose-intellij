package by.overpass.svgtocomposeintellij.generator.presentation.validation

data class Validatable<Value : Any, Error : Any>(
    val value: Value,
    val isValid: Boolean = true,
    val error: Error? = null,
)
