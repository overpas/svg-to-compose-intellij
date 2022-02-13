package by.overpass.svgtocomposeintellij.ui

import com.android.tools.adtui.validation.Validator
import org.junit.Assert.assertEquals
import org.junit.Test

class ValidationTest {

    @Test
    fun `test not empty string validator returns result ok`() {
        val validator = notEmptyStringValidator("prop")

        val result = validator.validate("value")

        assertEquals(Validator.Result.OK, result)
    }

    @Test
    fun `test not empty string validator returns result error`() {
        val validator = notEmptyStringValidator("prop")

        val result = validator.validate("")

        assertEquals(Validator.Result.fromNullableMessage("prop can't be empty"), result)
    }

    @Test
    fun `test non equal validator returns result ok`() {
        val obj1 = Any()
        val obj2 = Any()
        val validator = nonEqualValidator(obj1, "message")

        val result = validator.validate(obj2)

        assertEquals(Validator.Result.OK, result)
    }

    @Test
    fun `test non equal validator returns result error`() {
        val obj1 = Any()
        val validator = nonEqualValidator(obj1, "message")

        val result = validator.validate(obj1)

        assertEquals(Validator.Result.fromNullableMessage("message"), result)
    }
}