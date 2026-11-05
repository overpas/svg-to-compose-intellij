package by.overpass.svgtocomposeintellij.generator.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File

class SvgToComposeDataProcessorImplTest {

    private val data = SvgToComposeData(
        applicationIconPackage = "",
        accessorName = "",
        outputDir = File(""),
        vectorsDir = File(""),
        vectorImageType = VectorImageType.SVG,
        allAssetsPropertyName = "",
        isStringAccessorGenerated = false,
        isPreviewGenerated = true,
    )
    private val sourceRootResolver = mock<SourceRootResolver>()

    private val sut = SvgToComposeDataProcessorImpl(sourceRootResolver)

    @Test
    fun `data unchanged when output dir is not under a source root`() {
        val actual = sut(data)

        assertEquals(data, actual)
    }

    @Test
    fun `data unchanged when output dir is the source root`() {
        val input = data.copy(outputDir = File("/project/src/main/kotlin"))

        val actual = sut(input)

        assertEquals(input, actual)
    }

    @Test
    fun `package and output dir come from the conventional source root`() {
        val input = data.copy(outputDir = File("C:/Users/User/Projects/Project/src/kotlin/com/example/icons"))
        val expected = input.copy(
            applicationIconPackage = "com.example.icons",
            outputDir = File("C:/Users/User/Projects/Project/src/kotlin"),
        )

        val actual = sut(input)

        assertEquals(expected, actual)
    }

    @Test
    fun `kotlin package segment is kept`() {
        val input = data.copy(outputDir = File("/project/src/main/kotlin/com/example/kotlin/icons"))
        val expected = input.copy(
            applicationIconPackage = "com.example.kotlin.icons",
            outputDir = File("/project/src/main/kotlin"),
        )

        val actual = sut(input)

        assertEquals(expected, actual)
    }

    @Test
    fun `java package segment is kept`() {
        val input = data.copy(outputDir = File("/project/src/main/java/com/example/java/icons"))
        val expected = input.copy(
            applicationIconPackage = "com.example.java.icons",
            outputDir = File("/project/src/main/java"),
        )

        val actual = sut(input)

        assertEquals(expected, actual)
    }

    @Test
    fun `multiplatform source set is a conventional source root`() {
        val input = data.copy(outputDir = File("/project/src/commonMain/kotlin/com/example/icons"))
        val expected = input.copy(
            applicationIconPackage = "com.example.icons",
            outputDir = File("/project/src/commonMain/kotlin"),
        )

        val actual = sut(input)

        assertEquals(expected, actual)
    }

    @Test
    fun `java dir above the project is not a source root`() {
        val input = data.copy(outputDir = File("/home/user/java/project/src/main/kotlin/com/example/icons"))
        val expected = input.copy(
            applicationIconPackage = "com.example.icons",
            outputDir = File("/home/user/java/project/src/main/kotlin"),
        )

        val actual = sut(input)

        assertEquals(expected, actual)
    }

    @Test
    fun `resolved source root wins over the convention`() {
        val sourceRoot = File("/project/src/main/kotlin/generated")
        val input = data.copy(outputDir = File("/project/src/main/kotlin/generated/com/example/icons"))
        val expected = input.copy(
            applicationIconPackage = "com.example.icons",
            outputDir = sourceRoot,
        )
        whenever(sourceRootResolver.resolve(any())).thenReturn(sourceRoot)

        val actual = sut(input)

        assertEquals(expected, actual)
    }

    @Test
    fun `source root that does not contain the output dir is ignored`() {
        val input = data.copy(outputDir = File("/project/src/main/kotlin/com/example/icons"))
        val expected = input.copy(
            applicationIconPackage = "com.example.icons",
            outputDir = File("/project/src/main/kotlin"),
        )
        whenever(sourceRootResolver.resolve(any())).thenReturn(File("/elsewhere/src"))

        val actual = sut(input)

        assertEquals(expected, actual)
    }
}
