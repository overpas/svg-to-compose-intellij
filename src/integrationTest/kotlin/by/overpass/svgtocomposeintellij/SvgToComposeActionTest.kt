package by.overpass.svgtocomposeintellij

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SvgToComposeActionTest {

    private val sut = GeneratorScenarios()

    @Test
    fun newActionOpensGeneratorDialog() {
        val isShown = sut.isDialogShown()

        assertTrue(isShown)
    }

    @Test
    fun generateIsDisabledWithEmptyInput() {
        val isEnabled = sut.isGenerateEnabledWithEmptyInput()

        assertFalse(isEnabled)
    }

    @Test
    fun cancelWritesNoFiles() {
        val expected = emptyList<String>()

        val files = sut.filesAfterCancel()

        assertEquals(expected, files)
    }

    @Test
    fun generateWritesIconFiles() {
        val expected = listOf("__MyIconPack.kt", "myiconpack/AbTesting.kt")

        val files = sut.filesAfterGenerate()

        assertEquals(expected, files)
    }
}
