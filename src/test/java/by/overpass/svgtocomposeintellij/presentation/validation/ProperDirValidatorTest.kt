package by.overpass.svgtocomposeintellij.presentation.validation

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ProperDirValidatorTest(
    private val file: File,
    private val validationResult: ValidationResult<ProperDirValidator.Error>,
) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(
                File(""),
                ValidationResult.Error(ProperDirValidator.Error.Empty),
            ),
            arrayOf(
                File("src/test/resources/test_file.txt"),
                ValidationResult.Error(ProperDirValidator.Error.NotADirectory),
            ),
            arrayOf(
                File("src/test/resources/error"),
                ValidationResult.Error(ProperDirValidator.Error.InvalidPath),
            ),
            arrayOf(
                File("src/test/resources/"),
                ValidationResult.Ok,
            ),
        )
    }

    private val stubFile = File("")
    private val properDirValidator = ProperDirValidator(stubFile)

    @Test
    fun `test passed directory is validated correctly`() {
        val actual = properDirValidator.validate(file)

        assertEquals(validationResult, actual)
    }
}