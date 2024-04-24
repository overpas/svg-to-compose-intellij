package by.overpass.svgtocomposeintellij.preview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.preview.presentation.ComposeImageVectorPreviewState
import by.overpass.svgtocomposeintellij.preview.presentation.ComposeImageVectorPreviewViewModel
import com.intellij.icons.AllIcons
import com.intellij.ui.JBColor
import org.jetbrains.jewel.bridge.toComposeColor
import org.jetbrains.jewel.ui.component.CircularProgressIndicator
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Typography

@Composable
fun ComposeImageVectorPreview(viewModel: ComposeImageVectorPreviewViewModel, modifier: Modifier = Modifier) {
    val bgColor by remember(JBColor.PanelBackground.rgb) {
        mutableStateOf(JBColor.PanelBackground.toComposeColor())
    }
    Box(
        modifier = modifier.background(color = bgColor),
    ) {
        val state by viewModel.state.collectAsState()
        when (val theState = state) {
            is ComposeImageVectorPreviewState.Rendering -> {
                PreviewRendering(Modifier.fillMaxSize())
            }
            is ComposeImageVectorPreviewState.Error -> {
                PreviewError(Modifier.fillMaxSize())
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

@Composable
private fun PreviewRendering(modifier: Modifier = Modifier) {
    PreviewContent(
        label = Bundle.message("preview_state_progress"),
        modifier = modifier,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize()
                .padding(32.dp),
        )
    }
}

@Composable
private fun PreviewError(modifier: Modifier = Modifier) {
    PreviewContent(
        label = Bundle.message("preview_state_error"),
        modifier = modifier,
    ) {
        Icon(
            resource = "general/error.svg",
            contentDescription = Bundle.message("preview_state_error_content_description"),
            iconClass = AllIcons::class.java,
            modifier = Modifier.fillMaxSize()
                .padding(32.dp),
        )
    }
}

@Composable
private fun PreviewIcon(
    iconName: String,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    PreviewContent(
        label = iconName,
        modifier = modifier,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = iconName,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun PreviewContent(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(bottom = 8.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
        Text(
            text = label,
            style = Typography.h2TextStyle(),
        )
    }
}
