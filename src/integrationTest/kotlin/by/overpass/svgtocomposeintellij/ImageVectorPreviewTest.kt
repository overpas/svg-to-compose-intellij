package by.overpass.svgtocomposeintellij

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImageVectorPreviewTest {

    private val sut = PreviewScenarios()

    @Test
    fun imageVectorFileGetsPreviewEditor() {
        val hasPreview = sut.hasPreviewEditor("Note.kt")

        assertTrue(hasPreview)
    }

    @Test
    fun plainKotlinFileHasNoPreviewEditor() {
        val hasPreview = sut.hasPreviewEditor("Plain.kt")

        assertFalse(hasPreview)
    }
}
