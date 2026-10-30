package by.overpass.svgtocomposeintellij.preview.data

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class ColorConversionNegativeTest(private val colorString: String) {

    private val sut = ::hexToLongColor

    @Test
    fun `fails on a strings that doesn't represent a color`() {
        val result = runCatching { sut(colorString) }

        assertTrue(result.isFailure)
    }

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> =
            listOf(
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
}
