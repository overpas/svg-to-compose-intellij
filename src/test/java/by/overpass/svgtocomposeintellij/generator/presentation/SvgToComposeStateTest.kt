package by.overpass.svgtocomposeintellij.generator.presentation

import by.overpass.svgtocomposeintellij.generator.presentation.validation.Validatable
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SvgToComposeStateTest(
    private val state: SvgToComposeState,
    private val isValid: Boolean,
) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(
                DataInput(
                    accessorName = Validatable("", isValid = false),
                    outputDir = Validatable("dir"),
                    vectorImagesDir = Validatable("dir"),
                    allAssetsPropertyName = Validatable("all"),
                ),
                false,
            ),
            arrayOf(
                DataInput(
                    accessorName = Validatable("acc"),
                    outputDir = Validatable("", isValid = false),
                    vectorImagesDir = Validatable("dir"),
                    allAssetsPropertyName = Validatable("all"),
                ),
                false,
            ),
            arrayOf(
                DataInput(
                    accessorName = Validatable("acc"),
                    outputDir = Validatable("dir"),
                    vectorImagesDir = Validatable("", isValid = false),
                    allAssetsPropertyName = Validatable("all"),
                ),
                false,
            ),
            arrayOf(
                DataInput(
                    accessorName = Validatable("acc"),
                    outputDir = Validatable("dir"),
                    vectorImagesDir = Validatable("dir"),
                    allAssetsPropertyName = Validatable("", isValid = false),
                ),
                false,
            ),
            arrayOf(
                Error(RuntimeException("Test Exception")),
                false,
            ),
            arrayOf(
                Finished,
                false,
            ),
            arrayOf(
                DataInput(
                    accessorName = Validatable("acc"),
                    outputDir = Validatable("dir"),
                    vectorImagesDir = Validatable("dir"),
                    allAssetsPropertyName = Validatable("all"),
                ),
                true,
            ),
        )
    }

    @Test
    fun test() {
        val actual = state.isValid

        assertEquals(isValid, actual)
    }
}
