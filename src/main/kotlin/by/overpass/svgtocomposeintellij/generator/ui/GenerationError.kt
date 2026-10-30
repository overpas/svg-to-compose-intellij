package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import by.overpass.svgtocomposeintellij.generator.presentation.Error
import org.jetbrains.jewel.ui.component.Text

@Composable
internal fun GenerationError(
    error: Error,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Text(error.throwable.toString())
    }
}
