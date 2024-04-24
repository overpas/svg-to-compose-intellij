package by.overpass.svgtocomposeintellij.generator.presentation

import app.cash.turbine.test
import by.overpass.svgtocomposeintellij.generator.domain.SvgIconsGenerator
import by.overpass.svgtocomposeintellij.generator.domain.VectorImageType
import by.overpass.svgtocomposeintellij.generator.domain.VectorImageTypeDetector
import by.overpass.svgtocomposeintellij.generator.presentation.validation.DirError
import by.overpass.svgtocomposeintellij.generator.presentation.validation.Validatable
import by.overpass.svgtocomposeintellij.generator.presentation.validation.ValidationResult
import by.overpass.svgtocomposeintellij.generator.presentation.validation.ValueValidator
import java.io.File
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking

class SvgToComposeViewModelImplTest {

    private val targetDir = File("/dir")
    private val svgIconsGenerator = mock<SvgIconsGenerator>()
    private val vectorImageTypeDetector = mock<VectorImageTypeDetector>()
    private val nonStringEmptyValidator = mock<ValueValidator<String, Unit>>()
    private val directoryValidator = mock<ValueValidator<String, DirError>>()

    private val testDispatcher = StandardTestDispatcher()

    private val viewModel = SvgToComposeViewModelImpl(
        targetDir = targetDir,
        svgIconsGenerator = svgIconsGenerator,
        vectorImageTypeDetector = vectorImageTypeDetector,
        nonStringEmptyValidator = nonStringEmptyValidator,
        directoryValidator = directoryValidator,
        dispatcher = testDispatcher,
    )

    @Test
    fun `accessorName updated to a valid value`() {
        val expected = DataInput(
            accessorName = Validatable("NewAccessorName"),
            outputDir = Validatable("/dir"),
        )
        whenever(nonStringEmptyValidator.validate(any())).thenReturn(ValidationResult.Ok)

        viewModel.onAccessorNameChanged("NewAccessorName")
        val actual = viewModel.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `accessorName updated to an invalid value`() {
        val expected = DataInput(
            accessorName = Validatable("", isValid = false, error = Unit),
            outputDir = Validatable("/dir"),
        )
        whenever(nonStringEmptyValidator.validate(any())).thenReturn(ValidationResult.Error(Unit))

        viewModel.onAccessorNameChanged("")
        val actual = viewModel.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `allAssetsPropertyName updated to a valid value`() {
        val expected = DataInput(
            allAssetsPropertyName = Validatable("New"),
            outputDir = Validatable("/dir"),
        )
        whenever(nonStringEmptyValidator.validate(any())).thenReturn(ValidationResult.Ok)

        viewModel.onAllAssetsPropertyNameChanged("New")
        val actual = viewModel.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `allAssetsPropertyName updated to an invalid value`() {
        val expected = DataInput(
            allAssetsPropertyName = Validatable("", isValid = false, error = Unit),
            outputDir = Validatable("/dir"),
        )
        whenever(nonStringEmptyValidator.validate(any())).thenReturn(ValidationResult.Error(Unit))

        viewModel.onAllAssetsPropertyNameChanged("")
        val actual = viewModel.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `outputDir updated to a valid value`() {
        val expected = DataInput(
            outputDir = Validatable("/new/dir"),
        )
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)

        viewModel.onOutputDirChanged("/new/dir")
        val actual = viewModel.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `outputDir updated to an invalid value`() {
        val expected = DataInput(
            outputDir = Validatable("", isValid = false, error = DirError.Empty),
        )
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Error(DirError.Empty))

        viewModel.onOutputDirChanged("")
        val actual = viewModel.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `vectorImagesDirChanged updated to a valid value`() {
        val expected = DataInput(
            outputDir = Validatable("/dir"),
            vectorImagesDir = Validatable("/new/dir"),
        )
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)

        viewModel.onVectorImagesDirChanged("/new/dir")
        val actual = viewModel.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `vectorImageTypeDetector is triggered when vectorImagesDirChanged updated to a valid value`() {
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)

        viewModel.onVectorImagesDirChanged("/new/dir")

        verify(vectorImageTypeDetector).detect("/new/dir")
    }

    @Test
    fun `vectorImageType is updated when vectorImagesDirChanged updated to a valid value`() {
        val expected = DataInput(
            outputDir = Validatable("/dir"),
            vectorImagesDir = Validatable("/new/dir"),
            vectorImageType = VectorImageType.DRAWABLE,
        )
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)
        whenever(vectorImageTypeDetector.detect(any())).thenReturn(VectorImageType.DRAWABLE)

        viewModel.onVectorImagesDirChanged("/new/dir")
        val actual = viewModel.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `vectorImagesDirChanged updated to an invalid value`() {
        val expected = DataInput(
            outputDir = Validatable("/dir"),
            vectorImagesDir = Validatable("", isValid = false, error = DirError.Empty),
        )
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Error(DirError.Empty))

        viewModel.onVectorImagesDirChanged("")
        val actual = viewModel.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `vectorImageTypeDetector is NOT triggered when vectorImagesDirChanged updated to an invalid value`() {

        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Error(DirError.Empty))

        viewModel.onVectorImagesDirChanged("")

        verifyNoInteractions(vectorImageTypeDetector)
    }

    @Test
    fun `vectorImageType updated`() {
        val expected = DataInput(
            outputDir = Validatable("/dir"),
            vectorImageType = VectorImageType.DRAWABLE,
        )

        viewModel.onVectorImageTypeChanged(VectorImageType.DRAWABLE)
        val actual = viewModel.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `coroutineScope is closed when ViewModel is cleared`() {
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)
        viewModel.onVectorImagesDirChanged("/dir")

        viewModel.onCleared()
        viewModel.generate()

        verifyNoInteractions(svgIconsGenerator)
    }

    @Test
    fun `progress is shown and svg icons generated successfully`() = runTest(testDispatcher) {
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)
        viewModel.onVectorImagesDirChanged("/dir")
        wheneverBlocking { svgIconsGenerator.generate(any()) }.thenReturn(Result.success(Unit))

        viewModel.state.test {
            val initialState = DataInput(
                outputDir = Validatable("/dir"),
                vectorImagesDir = Validatable("/dir"),
                isInProgress = false,
            )
            assertEquals(initialState, awaitItem())

            viewModel.generate()

            val awaitItem = awaitItem()
            assertEquals(initialState.copy(isInProgress = true), awaitItem)
            assertEquals(Finished, awaitItem())
        }
    }

    @Test
    fun `progress is shown and svg icons generation failed`() = runTest(testDispatcher) {
        whenever(directoryValidator.validate(any())).thenReturn(ValidationResult.Ok)
        viewModel.onVectorImagesDirChanged("/dir")
        val exception = RuntimeException("Test")
        wheneverBlocking { svgIconsGenerator.generate(any()) }.thenReturn(Result.failure(exception))

        viewModel.state.test {
            val initialState = DataInput(
                outputDir = Validatable("/dir"),
                vectorImagesDir = Validatable("/dir"),
                isInProgress = false,
            )
            assertEquals(initialState, awaitItem())

            viewModel.generate()

            val awaitItem = awaitItem()
            assertEquals(initialState.copy(isInProgress = true), awaitItem)
            assertEquals(Error(exception), awaitItem())
        }
    }
}
