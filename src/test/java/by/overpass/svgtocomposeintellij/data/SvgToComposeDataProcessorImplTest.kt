package by.overpass.svgtocomposeintellij.data

import br.com.devsrsouza.svg2compose.VectorType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class SvgToComposeDataProcessorImplTest {

    private val processor = SvgToComposeDataProcessor()

    @Test
    fun `test data unchanged when package is empty`() {
        val data = SvgToComposeData(
            applicationIconPackage = "",
            accessorName = "",
            outputDir = File(""),
            vectorsDir = File(""),
            vectorImageType = VectorType.SVG,
            allAssetsPropertyName = "",
        )

        assertEquals(data, processor(data))
    }

    @Test
    fun `test package and outputDir updated when package is not empty`() {
        val data = SvgToComposeData(
            applicationIconPackage = "",
            accessorName = "",
            outputDir = File("C:/Users/User/Projects/Project/src/kotlin/com/example/icons"),
            vectorsDir = File(""),
            vectorImageType = VectorType.SVG,
            allAssetsPropertyName = "",
        )
        val expected = SvgToComposeData(
            applicationIconPackage = "com.example.icons",
            accessorName = "",
            outputDir = File("C:/Users/User/Projects/Project/src/kotlin"),
            vectorsDir = File(""),
            vectorImageType = VectorType.SVG,
            allAssetsPropertyName = "",
        )

        assertEquals(expected, processor(data))
    }
}