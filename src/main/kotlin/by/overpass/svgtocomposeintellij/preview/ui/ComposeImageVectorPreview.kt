package by.overpass.svgtocomposeintellij.preview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

@Composable
private fun PreviewRendering(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.weight(1f)
                .fillMaxWidth()
                .padding(32.dp),
        )
        Text(
            text = Bundle.message("preview_state_progress"),
            style = Typography.h2TextStyle(),
        )
    }
}

@Composable
private fun PreviewError(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            resource = "general/error.svg",
            contentDescription = Bundle.message("preview_state_error_content_description"),
            iconClass = AllIcons::class.java,
            modifier = Modifier.weight(1f)
                .fillMaxWidth()
                .padding(32.dp),
        )
        Text(
            text = Bundle.message("preview_state_error"),
            style = Typography.h2TextStyle(),
        )
    }
}

@Composable
private fun PreviewIcon(
    iconName: String,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = iconName,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        Text(
            text = iconName,
            style = Typography.h2TextStyle(),
        )
    }
}

@Composable
private fun PreviewContent(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                resource = "general/notificationWarning.svg",
                contentDescription = Bundle.message("preview_warning_icon_content_description"),
                iconClass = AllIcons::class.java,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = Bundle.message("preview_warning_text"),
            )
        }
        content()
    }
}
