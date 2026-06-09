package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.CheckboxRow
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.IndeterminateHorizontalProgressBar
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.icon.PathIconKey

internal const val WEIGHT_LEFT = 1f
internal const val WEIGHT_RIGHT = 3f

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
            onGenerateStringAccessorChange = viewModel::onGenerateStringAccessorChanged,
            onGeneratePreviewChange = viewModel::onGeneratePreviewChanged,
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
    onGenerateStringAccessorChange: (Boolean) -> Unit,
    onGeneratePreviewChange: (Boolean) -> Unit,
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
                onGenerateStringAccessorChange = onGenerateStringAccessorChange,
                onGeneratePreviewChange = onGeneratePreviewChange,
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
    onGenerateStringAccessorChange: (Boolean) -> Unit,
    onGeneratePreviewChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = Bundle.message("generator_accessor_name_info"),
        )
        StringPropertyRow(
            propertyName = Bundle.message("generator_accessor_name_hint"),
            value = dataInput.accessorName,
            onValueChange = onAccessorNameChange,
        )
        BrowseDirRow(
            propertyName = Bundle.message("generator_output_directory_hint"),
            dir = dataInput.outputDir,
            onDirChange = onOutputDirChange,
        )
        BrowseDirRow(
            propertyName = Bundle.message("generator_vector_images_directory_hint"),
            dir = dataInput.vectorImagesDir,
            onDirChange = onVectorImagesDirChange,
        )
        VectorImageTypeRow(
            vectorImageType = dataInput.vectorImageType,
            onVectorImageTypeChange = onVectorImageTypeChange,
        )
        StringPropertyRow(
            propertyName = Bundle.message("generator_all_assets_property_hint"),
            value = dataInput.allAssetsPropertyName,
            onValueChange = onAllAssetsPropertyNameChange,
        )
        CheckboxRow(
            checked = dataInput.generateStringAccessor,
            onCheckedChange = onGenerateStringAccessorChange,
        ) {
            Text(Bundle.message("generator_generate_string_accessor_checkbox_text"))
        }
        CheckboxRow(
            checked = dataInput.generatePreview,
            onCheckedChange = onGeneratePreviewChange,
        ) {
            Text(Bundle.message("generator_generate_preview_checkbox_text"))
        }
    }
}

@OptIn(ExperimentalJewelApi::class)
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
