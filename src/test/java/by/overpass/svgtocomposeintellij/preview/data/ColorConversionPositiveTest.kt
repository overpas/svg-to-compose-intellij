package by.overpass.svgtocomposeintellij.preview.data

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ColorConversionPositiveTest(
    private val colorString: String,
    private val expected: Long,
) {

    private val sut = ::hexToLongColor

    @Test
    fun `colors are parsed correctly`() {
        val actual = sut(colorString)

        assertEquals(expected, actual)
    }

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> =
            listOf(
                arrayOf(
                    "0xFF000000",
                    4_278_190_080L,
                ),
                arrayOf(
                    "0x00000000",
                    0L,
                ),
                arrayOf(
                    "0xFFFFFFFF",
                    4_294_967_295L,
                ),
                arrayOf(
                    "0xff000000",
                    4_278_190_080L,
                ),
                arrayOf(
                    "0xffffffff",
                    4_294_967_295L,
                ),
            )
    }
}
