package by.overpass.svgtocomposeintellij.generator.presentation

import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import by.overpass.svgtocomposeintellij.generator.domain.SvgIconsGenerator
import by.overpass.svgtocomposeintellij.generator.domain.VectorImageType
import by.overpass.svgtocomposeintellij.generator.domain.VectorImageTypeDetector
import by.overpass.svgtocomposeintellij.generator.presentation.validation.DirError
import by.overpass.svgtocomposeintellij.generator.presentation.validation.Validatable
import by.overpass.svgtocomposeintellij.generator.presentation.validation.ValidationResult
import by.overpass.svgtocomposeintellij.generator.presentation.validation.ValueValidator
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.io.File

class SvgToComposeViewModelImplTest {

    private val targetDir = File("${File.pathSeparator}dir")
    private val svgIconsGenerator = mock<SvgIconsGenerator>()
    private val vectorImageTypeDetector = mock<VectorImageTypeDetector>()
    private val nonStringEmptyValidator = mock<ValueValidator<String, Unit>>()
    private val directoryValidator = mock<ValueValidator<String, DirError>>()

    private val sut = SvgToComposeViewModelImpl(
        targetDir = targetDir,
        svgIconsGenerator = svgIconsGenerator,
        vectorImageTypeDetector = vectorImageTypeDetector,
        nonStringEmptyValidator = nonStringEmptyValidator,
        directoryValidator = directoryValidator,
    )

    @Test
    fun `accessorName updated to a valid value`() {
        val expected = DataInput(
            accessorName = Validatable("NewAccessorName"),
            outputDir = Validatable("${File.pathSeparator}dir"),
        )
        whenever(nonStringEmptyValidator.validate(any())).thenReturn(ValidationResult.Ok)

        sut.onAccessorNameChanged("NewAccessorName")
        val actual = sut.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `accessorName updated to an invalid value`() {
        val expected = DataInput(
            accessorName = Validatable("", isValid = false, error = Unit),
            outputDir = Validatable("${File.pathSeparator}dir"),
        )
        whenever(nonStringEmptyValidator.validate(any())).thenReturn(ValidationResult.Error(Unit))

        sut.onAccessorNameChanged("")
        val actual = sut.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `allAssetsPropertyName updated to a valid value`() {
        val expected = DataInput(
            allAssetsPropertyName = Validatable("New"),
            outputDir = Validatable("${File.pathSeparator}dir"),
        )
        whenever(nonStringEmptyValidator.validate(any())).thenReturn(ValidationResult.Ok)

        sut.onAllAssetsPropertyNameChanged("New")
        val actual = sut.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `allAssetsPropertyName updated to an invalid value`() {
        val expected = DataInput(
            allAssetsPropertyName = Validatable("", isValid = false, error = Unit),
            outputDir = Validatable("${File.pathSeparator}dir"),
        )
        whenever(nonStringEmptyValidator.validate(any())).thenReturn(ValidationResult.Error(Unit))

        sut.onAllAssetsPropertyNameChanged("")
        val actual = sut.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `outputDir updated to a valid value`() {
        val expected = DataInput(
            outputDir = Validatable("${File.pathSeparator}new${File.pathSeparator}dir"),
        )
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)

        sut.onOutputDirChanged("${File.pathSeparator}new${File.pathSeparator}dir")
        val actual = sut.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `outputDir updated to an invalid value`() {
        val expected = DataInput(
            outputDir = Validatable("", isValid = false, error = DirError.Empty),
        )
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Error(DirError.Empty))

        sut.onOutputDirChanged("")
        val actual = sut.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `vectorImagesDirChanged updated to a valid value`() {
        val expected = DataInput(
            outputDir = Validatable("${File.pathSeparator}dir"),
            vectorImagesDir = Validatable("${File.pathSeparator}new${File.pathSeparator}dir"),
        )
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)

        sut.onVectorImagesDirChanged("${File.pathSeparator}new${File.pathSeparator}dir")
        val actual = sut.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `vectorImageTypeDetector is triggered when vectorImagesDirChanged updated to a valid value`() {
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)

        sut.onVectorImagesDirChanged("${File.pathSeparator}new${File.pathSeparator}dir")

        verify(vectorImageTypeDetector) {
            1 * { detect("${File.pathSeparator}new${File.pathSeparator}dir") }
        }
    }

    @Test
    fun `vectorImageType is updated when vectorImagesDirChanged updated to a valid value`() {
        val expected = DataInput(
            outputDir = Validatable("${File.pathSeparator}dir"),
            vectorImagesDir = Validatable("${File.pathSeparator}new${File.pathSeparator}dir"),
            vectorImageType = VectorImageType.DRAWABLE,
        )
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)
        whenever(vectorImageTypeDetector.detect(any())).thenReturn(VectorImageType.DRAWABLE)

        sut.onVectorImagesDirChanged("${File.pathSeparator}new${File.pathSeparator}dir")
        val actual = sut.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `vectorImagesDirChanged updated to an invalid value`() {
        val expected = DataInput(
            outputDir = Validatable("${File.pathSeparator}dir"),
            vectorImagesDir = Validatable("", isValid = false, error = DirError.Empty),
        )
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Error(DirError.Empty))

        sut.onVectorImagesDirChanged("")
        val actual = sut.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `vectorImageTypeDetector is NOT triggered when vectorImagesDirChanged updated to an invalid value`() {
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Error(DirError.Empty))

        sut.onVectorImagesDirChanged("")

        verifyNoInteractions(vectorImageTypeDetector)
    }

    @Test
    fun `vectorImageType updated`() {
        val expected = DataInput(
            outputDir = Validatable("${File.pathSeparator}dir"),
            vectorImageType = VectorImageType.DRAWABLE,
        )

        sut.onVectorImageTypeChanged(VectorImageType.DRAWABLE)
        val actual = sut.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `progress is shown and svg icons generated successfully`() =
        runTest {
            turbineScope {
                val expected = listOf(
                    DataInput(
                        outputDir = Validatable("${File.pathSeparator}dir"),
                        vectorImagesDir = Validatable("${File.pathSeparator}dir"),
                        isInProgress = true,
                    ),
                    Finished,
                )
                whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)
                sut.onVectorImagesDirChanged("${File.pathSeparator}dir")
                whenever { svgIconsGenerator.generate(any()) }.thenReturn(Result.success(Unit))
                val states = sut.state.testIn(backgroundScope)
                states.skipItems(1)

                sut.onGenerate()
                val actual = listOf(states.awaitItem(), states.awaitItem())

                assertEquals(expected, actual)
            }
        }

    @Test
    fun `progress is shown and svg icons generation failed`() =
        runTest {
            turbineScope {
                val exception = RuntimeException("Test")
                val expected = listOf(
                    DataInput(
                        outputDir = Validatable("${File.pathSeparator}dir"),
                        vectorImagesDir = Validatable("${File.pathSeparator}dir"),
                        isInProgress = true,
                    ),
                    Error(exception),
                )
                whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)
                sut.onVectorImagesDirChanged("${File.pathSeparator}dir")
                whenever { svgIconsGenerator.generate(any()) }.thenReturn(runCatching { throw exception })
                val states = sut.state.testIn(backgroundScope)
                states.skipItems(1)

                sut.onGenerate()
                val actual = listOf(states.awaitItem(), states.awaitItem())

                assertEquals(expected, actual)
            }
        }
}
