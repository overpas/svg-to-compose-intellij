package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.generator.domain.VectorImageType
import by.overpass.svgtocomposeintellij.generator.presentation.DataInput
import by.overpass.svgtocomposeintellij.generator.presentation.Error
import by.overpass.svgtocomposeintellij.generator.presentation.Finished
import by.overpass.svgtocomposeintellij.generator.presentation.SvgToComposeViewModel
import by.overpass.svgtocomposeintellij.generator.presentation.validation.DirError
import by.overpass.svgtocomposeintellij.generator.presentation.validation.Validatable
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.intellij.icons.AllIcons
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.IndeterminateHorizontalProgressBar
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.icon.PathIconKey

private const val WEIGHT_LEFT = 1f
private const val WEIGHT_RIGHT = 3f

@Composable
fun SvgToComposePlugin(viewModel: SvgToComposeViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    when (state) {
        is DataInput -> DataInputWithProgress(
            dataInput = state as DataInput,
            onAccessorNameChange = viewModel::onAccessorNameChanged,
            onOutputDirChange = viewModel::onOutputDirChanged,
            onVectorImagesDirChange = viewModel::onVectorImagesDirChanged,
            onVectorImageTypeChange = viewModel::onVectorImageTypeChanged,
            onAllAssetsPropertyNameChange = viewModel::onAllAssetsPropertyNameChanged,
            modifier = modifier,
        )

        is Error -> GenerationError(
            error = state as Error,
            modifier = modifier,
        )

        is Finished -> Unit
    }
}

@Composable
private fun DataInputWithProgress(
    dataInput: DataInput,
    onAccessorNameChange: (String) -> Unit,
    onOutputDirChange: (String) -> Unit,
    onVectorImagesDirChange: (String) -> Unit,
    onVectorImageTypeChange: (VectorImageType) -> Unit,
    onAllAssetsPropertyNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Dimmed(
            isDimmed = dataInput.isInProgress,
            modifier = Modifier.fillMaxSize().weight(1f),
        ) {
            DataInput(
                dataInput = dataInput,
                onAccessorNameChange = onAccessorNameChange,
                onOutputDirChange = onOutputDirChange,
                onVectorImagesDirChange = onVectorImagesDirChange,
                onVectorImageTypeChange = onVectorImageTypeChange,
                onAllAssetsPropertyNameChange = onAllAssetsPropertyNameChange,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (dataInput.isInProgress) {
            IndeterminateHorizontalProgressBar(
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun Dimmed(
    isDimmed: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier) {
        content()
        if (isDimmed) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(JewelTheme.globalColors.panelBackground.copy(alpha = 0.5F))
                    .disableClickAndRipple(),
            )
        }
    }
}

@Composable
private fun DataInput(
    dataInput: DataInput,
    onAccessorNameChange: (String) -> Unit,
    onOutputDirChange: (String) -> Unit,
    onVectorImagesDirChange: (String) -> Unit,
    onVectorImageTypeChange: (VectorImageType) -> Unit,
    onAllAssetsPropertyNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = Bundle.message("generator_accessor_name_info"),
        )
        Spacer(Modifier.height(8.dp))
        StringPropertyRow(
            propertyName = Bundle.message("generator_accessor_name_hint"),
            value = dataInput.accessorName,
            onValueChange = onAccessorNameChange,
        )
        Spacer(Modifier.height(8.dp))
        BrowseDirRow(
            propertyName = Bundle.message("generator_output_directory_hint"),
            dir = dataInput.outputDir,
            onDirChange = onOutputDirChange,
        )
        Spacer(Modifier.height(8.dp))
        BrowseDirRow(
            propertyName = Bundle.message("generator_vector_images_directory_hint"),
            dir = dataInput.vectorImagesDir,
            onDirChange = onVectorImagesDirChange,
        )
        Spacer(Modifier.height(8.dp))
        VectorImageTypeRow(
            vectorImageType = dataInput.vectorImageType,
            onVectorImageTypeChange = onVectorImageTypeChange,
        )
        Spacer(Modifier.height(8.dp))
        StringPropertyRow(
            propertyName = Bundle.message("generator_all_assets_property_hint"),
            value = dataInput.allAssetsPropertyName,
            onValueChange = onAllAssetsPropertyNameChange,
        )
    }
}

@Composable
private fun VectorImageTypeRow(
    vectorImageType: VectorImageType,
    onVectorImageTypeChange: (VectorImageType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = Bundle.message("generator_vector_image_type_hint"),
            modifier = Modifier.weight(WEIGHT_LEFT),
        )
        Dropdown(
            modifier = Modifier.weight(WEIGHT_RIGHT),
            menuContent = {
                VectorImageType.entries.forEach { imageType ->
                    selectableItem(
                        selected = imageType == vectorImageType,
                        onClick = { onVectorImageTypeChange(imageType) }) {
                        Text(imageType.name)
                    }
                }
            }
        ) {
            Text(
                text = vectorImageType.name,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BrowseDirRow(
    propertyName: String,
    dir: Validatable<String, DirError>,
    onDirChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = "$propertyName:",
            modifier = Modifier.weight(WEIGHT_LEFT),
        )
        var showFilePicker by remember { mutableStateOf(false) }
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
                onBrowseClick = { showFilePicker = !showFilePicker },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        DirectoryPicker(
            show = showFilePicker,
            initialDirectory = if (dir.isValid) dir.value else System.getProperty("user.home"),
            title = Bundle.message("generator_directory_title_template", propertyName),
            onFileSelected = { selectedDir ->
                if (selectedDir != null) {
                    dirValueState.setTextAndPlaceCursorAtEnd(selectedDir)
                } else {
                    showFilePicker = false
                }
            },
        )
    }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StringPropertyRow(
    propertyName: String,
    value: Validatable<String, Unit>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = "$propertyName:",
            modifier = Modifier.weight(WEIGHT_LEFT),
        )
        OptionalTooltip(
            tooltip = if (value.isValid) {
                null
            } else {
                {
                    Text(
                        Bundle.messagePointer(
                            "generator_property_invalid_empty_message",
                            propertyName
                        )
                            .get(),
                    )
                }
            },
            modifier = Modifier.weight(WEIGHT_RIGHT),
        ) {
            val stringValueState = rememberTextFieldState(value.value)
            LaunchedEffect(stringValueState) {
                snapshotFlow { stringValueState.text }.collect { charSequence ->
                    onValueChange(charSequence.toString())
                }
            }
            TextField(
                state = stringValueState,
                outline = if (value.isValid) Outline.None else Outline.Error,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun GenerationError(error: Error, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Text(error.throwable.toString())
    }
}
