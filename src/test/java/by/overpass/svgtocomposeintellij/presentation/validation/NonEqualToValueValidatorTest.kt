package by.overpass.svgtocomposeintellij.presentation.validation

import org.junit.Assert.assertEquals
import org.junit.Test

class NonEqualToValueValidatorTest {

    private val obj1 = Any()

    private val validator = NonEqualToValueValidator(obj1, "message")

    @Test
    fun `test non equal validator returns result ok`() {
        val obj2 = Any()

        val result = validator.validate(obj2)

        assertEquals(ValidationResult.Ok, result)
    }

    @Test
    fun `test non equal validator returns result error`() {
        val result = validator.validate(obj1)

        assertEquals(ValidationResult.Error("message"), result)
    }
}