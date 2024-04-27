package by.overpass.svgtocomposeintellij.preview.data

import kotlin.test.assertFailsWith
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ColorConversionNegativeTest(
    private val colorString: String,
) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(
                "0b11000000",
            ),
            arrayOf(
                "0x00",
            ),
            arrayOf(
                "abcd",
            ),
            arrayOf(
                "",
            ),
        )
    }

    @Test
    fun `fails on a strings that doesn't represent a color`() {
        assertFailsWith<Exception> {
            hexToLongColor(colorString)
        }
    }
}
