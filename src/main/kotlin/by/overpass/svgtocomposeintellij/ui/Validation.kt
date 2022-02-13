package by.overpass.svgtocomposeintellij.ui

import com.android.tools.adtui.validation.Validator

fun notEmptyStringValidator(propertyName: String): Validator<String> = stringValidator { value ->
    if (value.isEmpty()) {
        Validator.Result.fromNullableMessage("$propertyName can't be empty")
    } else {
        Validator.Result.OK
    }
}

inline fun stringValidator(crossinline validate: (String) -> Validator.Result) = object : Validator<String> {
    override fun validate(value: String): Validator.Result = validate(value)
}

fun <T> nonEqualValidator(wrongTarget: T, message: String): Validator<T> = object : Validator<T> {
    override fun validate(value: T): Validator.Result {
        return if (value == wrongTarget) {
            Validator.Result.fromNullableMessage(message)
        } else {
            Validator.Result.OK
        }
    }
}