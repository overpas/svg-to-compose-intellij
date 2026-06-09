package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.generator.presentation.validation.DirError
import by.overpass.svgtocomposeintellij.generator.presentation.validation.Validatable
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.icon.PathIconKey

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BrowseDirRow(
    propertyName: String,
    dir: Validatable<String, DirError>,
    onDirChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val project = LocalProject.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = "$propertyName:",
            modifier = Modifier.weight(WEIGHT_LEFT),
        )
        val dirValueState = rememberTextFieldState(dir.value)
        LaunchedEffect(dirValueState) {
            snapshotFlow { dirValueState.text }.collect { charSequence ->
                onDirChange(charSequence.toString())
            }
        }
        OptionalTooltip(
            tooltip = when (dir.error) {
                null -> null
                is DirError.Empty -> {
                    { Text(Bundle.message("generator_output_directory_invalid_empty_message")) }
                }

                is DirError.InvalidPath -> {
                    { Text(Bundle.message("generator_output_directory_invalid_path_message")) }
                }

                is DirError.NotADirectory -> {
                    { Text(Bundle.message("generator_output_directory_invalid_not_directory_message")) }
                }
            },
            modifier = Modifier.weight(WEIGHT_RIGHT),
        ) {
            DirTextField(
                dirValueState = dirValueState,
                outline = if (dir.isValid) Outline.None else Outline.Error,
                onBrowseClick = {
                    chooseDirectory(project, propertyName, dir)
                        ?.let(dirValueState::setTextAndPlaceCursorAtEnd)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun chooseDirectory(
    project: Project,
    propertyName: String,
    dir: Validatable<String, DirError>,
): String? {
    val initialPath = if (dir.isValid) {
        dir.value
    } else {
        System.getProperty("user.home")
    }
    val initialDirectory = LocalFileSystem.getInstance().findFileByPath(initialPath)
    val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
        .withTitle(Bundle.message("generator_directory_title_template", propertyName))
    return FileChooser.chooseFile(descriptor, project, initialDirectory)?.path
}

@Composable
private fun DirTextField(
    dirValueState: TextFieldState,
    outline: Outline,
    onBrowseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        state = dirValueState,
        trailingIcon = {
            IconButton(
                onClick = onBrowseClick
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
