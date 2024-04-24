package by.overpass.svgtocomposeintellij.preview.presentation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.cash.turbine.test
import by.overpass.svgtocomposeintellij.preview.data.IconData
import by.overpass.svgtocomposeintellij.preview.data.IconDataParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ComposeImageVectorPreviewViewModelImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val iconDataParser = mock<IconDataParser>()

    private val viewModel = ComposeImageVectorPreviewViewModelImpl(
        coroutineScope = testScope,
        iconDataParser = iconDataParser,
    )

    @Test
    fun `initial state is Rendering`() {
        val actual = viewModel.state.value

        assertEquals(ComposeImageVectorPreviewState.Rendering, actual)
    }

    @Test
    fun `ParseFile intent is received - icon is parsed - state is Icon`() = runTest(testDispatcher) {
        val iconData = IconData(
            name = "Test",
            imageVector = ImageVector.Builder(
                defaultWidth = 10.dp,
                defaultHeight = 10.dp,
                viewportWidth = 100f,
                viewportHeight = 100f,
            ).build()
        )
        val expected = ComposeImageVectorPreviewState.Icon(iconData)
        whenever(iconDataParser.parse()) doReturn Result.success(iconData)

        viewModel.dispatch(ComposeImageVectorPreviewIntent.ParseFile)

        viewModel.state.test {
            skipItems(1)

            val actual = awaitItem()

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `ParseFile intent is received - icon is NOT parsed - state is Error`() = runTest(testDispatcher) {
        val expected = ComposeImageVectorPreviewState.Error
        whenever(iconDataParser.parse()) doReturn Result.failure(IllegalStateException("Test exception"))

        viewModel.dispatch(ComposeImageVectorPreviewIntent.ParseFile)

        viewModel.state.test {
            skipItems(1)

            val actual = awaitItem()

            assertEquals(expected, actual)
        }
    }
}
