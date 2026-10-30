package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import by.overpass.svgtocomposeintellij.Bundle
import com.intellij.icons.AllIcons
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.icon.PathIconKey

@Composable
internal fun DirTextField(
    dirValueState: TextFieldState,
    outline: Outline,
    onBrowseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        state = dirValueState,
        trailingIcon = {
            IconButton(
                onClick = onBrowseClick,
            ) {
                Icon(
                    key = PathIconKey("expui/inline/browse.svg", AllIcons::class.java),
                    contentDescription = Bundle.message("generator_picker_button_content_description"),
                )
            }
        },
        outline = outline,
        modifier = modifier,
    )
}
