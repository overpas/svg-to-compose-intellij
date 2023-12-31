package by.overpass.svgtocomposeintellij.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class VectorImageTypeDetectorTest(
    private val path: String,
    private val expected: VectorImageType?,
) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any?>> = listOf(
            arrayOf(
                "src/test/resources/svg/",
                VectorImageType.SVG,
            ),
            arrayOf(
                "src/test/resources/xml/",
                VectorImageType.DRAWABLE,
            ),
            arrayOf(
                "src/test/resources/mixed/",
                null,
            ),
            arrayOf(
                "src/test/resources/svg-with-hidden-files/",
                VectorImageType.SVG,
            ),
        )
    }

    private val detector = VectorImageTypeDetector

    @Test
    fun test() {
        val actual = detector.detect(path)

        assertEquals(expected, actual)
    }
}
