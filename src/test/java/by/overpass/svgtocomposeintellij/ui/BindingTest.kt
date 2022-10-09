package by.overpass.svgtocomposeintellij.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class BindingTest {

    var property: String = "string"

    @Test
    fun `test string property binding get works`() {
        val binding = ::property.toBinding()

        assertEquals("string", binding.get())
    }

    @Test
    fun `test string property binding set works`() {
        val prop = ::property

        val binding = prop.toBinding()
        binding.set("string2")

        assertEquals("string2", prop.get())
    }
}