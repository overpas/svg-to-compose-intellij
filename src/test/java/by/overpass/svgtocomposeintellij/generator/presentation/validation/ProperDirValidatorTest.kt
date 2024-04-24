package by.overpass.svgtocomposeintellij.generator.presentation.validation

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ProperDirValidatorTest(
    private val file: String,
    private val validationResult: ValidationResult<DirError>,
) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(
                "",
                ValidationResult.Error(DirError.Empty),
            ),
            arrayOf(
                "src/test/resources/test_file.txt",
                ValidationResult.Error(DirError.NotADirectory),
            ),
            arrayOf(
                "src/test/resources/error",
                ValidationResult.Error(DirError.InvalidPath),
            ),
            arrayOf(
                "src/test/resources/",
                ValidationResult.Ok,
            ),
        )
    }

    private val properDirValidator = ProperDirValidator

    @Test
    fun `test passed directory is validated correctly`() {
        val actual = properDirValidator.validate(file)

        assertEquals(validationResult, actual)
    }
}
