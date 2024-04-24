package by.overpass.svgtocomposeintellij.generator.presentation.validation

import org.junit.Assert.assertEquals
import org.junit.Test

class CantBeEmptyStringValidatorTest {

    private val validator = CantBeEmptyStringValidator

    @Test
    fun `test not empty string validator returns result ok`() {
        val result = validator.validate("value")

        assertEquals(ValidationResult.Ok, result)
    }

    @Test
    fun `test not empty string validator returns result error`() {
        val result = validator.validate("")

        assertEquals(ValidationResult.Error(Unit), result)
    }
}
