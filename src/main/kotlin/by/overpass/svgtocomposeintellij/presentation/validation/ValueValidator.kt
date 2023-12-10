package by.overpass.svgtocomposeintellij.presentation.validation

sealed class ValidationResult<in V> {

    data object Ok : ValidationResult<Any>()

    data class Error<V>(val value: V) : ValidationResult<V>()
}

interface ValueValidator<in T, R> {

    fun validate(value: T): ValidationResult<R>
}