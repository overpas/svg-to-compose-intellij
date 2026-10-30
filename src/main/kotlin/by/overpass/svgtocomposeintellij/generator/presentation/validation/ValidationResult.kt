package by.overpass.svgtocomposeintellij.generator.presentation.validation

sealed interface ValidationResult<in V> {

    data object Ok : ValidationResult<Any>

    data class Error<V>(val error: V) : ValidationResult<V>
}
