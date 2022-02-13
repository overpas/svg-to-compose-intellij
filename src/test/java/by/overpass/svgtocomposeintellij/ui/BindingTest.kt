package by.overpass.svgtocomposeintellij.ui

import com.android.tools.idea.observable.core.ObjectValueProperty
import com.android.tools.idea.observable.core.StringValueProperty
import org.junit.Assert.assertEquals
import org.junit.Test

class BindingTest {

    @Test
    fun `test string property binding get works`() {
        val prop = StringValueProperty("string")

        val binding = prop.toBinding()

        assertEquals("string", binding.get())
    }

    @Test
    fun `test string property binding set works`() {
        val prop = StringValueProperty("string")

        val binding = prop.toBinding()
        binding.set("string2")

        assertEquals("string2", prop.get())
    }

    @Test
    fun `test object property binding get works`() {
        val prop = ObjectValueProperty(1)

        val binding = prop.toBinding()

        assertEquals(1, binding.get())
    }

    @Test
    fun `test object property binding set works`() {
        val prop = ObjectValueProperty(1)

        val binding = prop.toBinding()
        binding.set(2)

        assertEquals(2, prop.get())
    }
}