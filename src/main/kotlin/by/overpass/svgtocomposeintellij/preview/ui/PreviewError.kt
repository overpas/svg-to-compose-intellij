package by.overpass.svgtocomposeintellij.preview.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.overpass.svgtocomposeintellij.Bundle
import com.intellij.icons.AllIcons
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icon.PathIconKey
import org.jetbrains.jewel.ui.typography

@Composable
internal fun PreviewError(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            key = PathIconKey("general/error.svg", AllIcons::class.java),
            contentDescription = Bundle.message("preview_state_error_content_description"),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(32.dp),
        )
        Text(
            text = Bundle.message("preview_state_error"),
            style = JewelTheme.typography.h2TextStyle,
        )
    }
}
