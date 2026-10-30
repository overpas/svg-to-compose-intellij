package by.overpass.svgtocomposeintellij.preview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.overpass.svgtocomposeintellij.preview.presentation.ComposeImageVectorPreviewIntent
import by.overpass.svgtocomposeintellij.preview.presentation.ComposeImageVectorPreviewState
import by.overpass.svgtocomposeintellij.preview.presentation.ComposeImageVectorPreviewViewModel
import com.intellij.ui.JBColor
import org.jetbrains.jewel.bridge.toComposeColor

@Composable
fun ComposeImageVectorPreview(
    viewModel: ComposeImageVectorPreviewViewModel,
    modifier: Modifier = Modifier,
) {
    val bgColor by remember(JBColor.PanelBackground.rgb) {
        mutableStateOf(JBColor.PanelBackground.toComposeColor())
    }
    LaunchedEffect(viewModel) {
        viewModel.dispatch(ComposeImageVectorPreviewIntent.ParseFile)
    }
    PreviewContent(
        modifier = modifier.background(color = bgColor)
            .padding(8.dp),
    ) {
        val state by viewModel.state.collectAsState()
        when (val theState = state) {
            is ComposeImageVectorPreviewState.Rendering -> {
                PreviewRendering(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is ComposeImageVectorPreviewState.Error -> {
                PreviewError(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is ComposeImageVectorPreviewState.Icon -> {
                PreviewIcon(
                    iconName = theState.data.name,
                    imageVector = theState.data.imageVector,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
