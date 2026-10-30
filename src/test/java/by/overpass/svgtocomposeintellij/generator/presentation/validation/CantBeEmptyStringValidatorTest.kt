package by.overpass.svgtocomposeintellij.generator.presentation.validation

import org.junit.Assert.assertEquals
import org.junit.Test

class CantBeEmptyStringValidatorTest {

    private val sut = CantBeEmptyStringValidator

    @Test
    fun `test not empty string validator returns result ok`() {
        val result = sut.validate("value")

        assertEquals(ValidationResult.Ok, result)
    }

    @Test
    fun `test not empty string validator returns result error`() {
        val expected = ValidationResult.Error(Unit)

        val result = sut.validate("")

        assertEquals(expected, result)
    }
}
