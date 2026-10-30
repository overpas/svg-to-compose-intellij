package by.overpass.svgtocomposeintellij.preview.presentation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import by.overpass.svgtocomposeintellij.preview.data.IconData
import by.overpass.svgtocomposeintellij.preview.data.IconDataParser
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeImageVectorPreviewViewModelImplTest {

    private val iconDataParser = mock<IconDataParser>()

    private val sut = ComposeImageVectorPreviewViewModelImpl(iconDataParser = iconDataParser)

    @Test
    fun `initial state is Rendering`() {
        val expected = ComposeImageVectorPreviewState.Rendering

        val actual = sut.state.value

        assertEquals(expected, actual)
    }

    @Test
    fun `ParseFile intent is received - icon is parsed - state is Icon`() =
        runTest {
            val iconData = IconData(
                name = "Test",
                imageVector = ImageVector.Builder(
                    defaultWidth = 10.dp,
                    defaultHeight = 10.dp,
                    viewportWidth = 100f,
                    viewportHeight = 100f,
                ).build(),
            )
            val expected = ComposeImageVectorPreviewState.Icon(iconData)
            whenever(iconDataParser.parse()) doReturn Result.success(iconData)

            sut.dispatch(ComposeImageVectorPreviewIntent.ParseFile)
            val actual = sut.state.value

            assertEquals(expected, actual)
        }

    @Test
    fun `ParseFile intent is received - icon is NOT parsed - state is Error`() =
        runTest {
            val expected = ComposeImageVectorPreviewState.Error
            whenever(iconDataParser.parse()) doReturn runCatching { error("Test exception") }

            sut.dispatch(ComposeImageVectorPreviewIntent.ParseFile)
            val actual = sut.state.value

            assertEquals(expected, actual)
        }
}
