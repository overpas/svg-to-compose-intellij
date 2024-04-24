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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.intellij.ui.JBColor
import javax.swing.JComponent
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.bridge.toComposeColor
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.IndeterminateHorizontalProgressBar
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

private const val WEIGHT_LEFT = 1f
private const val WEIGHT_RIGHT = 3f

@OptIn(ExperimentalJewelApi::class)
fun svgToComposePluginPanel(viewModel: SvgToComposeViewModel): JComponent = JewelComposePanel {
    SwingBridgeTheme {
        val bgColor by remember(JBColor.PanelBackground.rgb) {
            mutableStateOf(JBColor.PanelBackground.toComposeColor())
        }
        SvgToComposePlugin(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
                .background(bgColor),
        )
    }
}

@Composable
private fun SvgToComposePlugin(viewModel: SvgToComposeViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    when (state) {
        is DataInput -> DataInputWithProgress(
            dataInput = state as DataInput,
            onAccessorNameChanged = viewModel::onAccessorNameChanged,
            onOutputDirChanged = viewModel::onOutputDirChanged,
            onVectorImagesDirChanged = viewModel::onVectorImagesDirChanged,
            onVectorImageTypeChanged = viewModel::onVectorImageTypeChanged,
            onAllAssetsPropertyNameChanged = viewModel::onAllAssetsPropertyNameChanged,
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
    onAccessorNameChanged: (String) -> Unit,
    onOutputDirChanged: (String) -> Unit,
    onVectorImagesDirChanged: (String) -> Unit,
    onVectorImageTypeChanged: (VectorImageType) -> Unit,
    onAllAssetsPropertyNameChanged: (String) -> Unit,
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
                onAccessorNameChanged = onAccessorNameChanged,
                onOutputDirChanged = onOutputDirChanged,
                onVectorImagesDirChanged = onVectorImagesDirChanged,
                onVectorImageTypeChanged = onVectorImageTypeChanged,
                onAllAssetsPropertyNameChanged = onAllAssetsPropertyNameChanged,
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
                    .background(JewelTheme.globalColors.paneBackground.copy(alpha = 0.5F))
                    .disableClickAndRipple(),
            )
        }
    }
}

@Composable
private fun DataInput(
    dataInput: DataInput,
    onAccessorNameChanged: (String) -> Unit,
    onOutputDirChanged: (String) -> Unit,
    onVectorImagesDirChanged: (String) -> Unit,
    onVectorImageTypeChanged: (VectorImageType) -> Unit,
    onAllAssetsPropertyNameChanged: (String) -> Unit,
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
            onValueChanged = onAccessorNameChanged,
        )
        Spacer(Modifier.height(8.dp))
        BrowseDirRow(
            propertyName = Bundle.message("generator_output_directory_hint"),
            dir = dataInput.outputDir,
            onDirChanged = onOutputDirChanged,
        )
        Spacer(Modifier.height(8.dp))
        BrowseDirRow(
            propertyName = Bundle.message("generator_vector_images_directory_hint"),
            dir = dataInput.vectorImagesDir,
            onDirChanged = onVectorImagesDirChanged,
        )
        Spacer(Modifier.height(8.dp))
        VectorImageTypeRow(
            vectorImageType = dataInput.vectorImageType,
            onVectorImageTypeChanded = onVectorImageTypeChanged,
        )
        Spacer(Modifier.height(8.dp))
        StringPropertyRow(
            propertyName = Bundle.message("generator_all_assets_property_hint"),
            value = dataInput.allAssetsPropertyName,
            onValueChanged = onAllAssetsPropertyNameChanged,
        )
    }
}

@Composable
private fun VectorImageTypeRow(
    vectorImageType: VectorImageType,
    onVectorImageTypeChanded: (VectorImageType) -> Unit,
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
                        onClick = { onVectorImageTypeChanded(imageType) }) {
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

@Composable
private fun BrowseDirRow(
    propertyName: String,
    dir: Validatable<String, DirError>,
    onDirChanged: (String) -> Unit,
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
        DirTextField(
            dir = dir,
            onValueChange = onDirChanged,
            modifier = Modifier.weight(WEIGHT_RIGHT),
        ) {
            showFilePicker = !showFilePicker
        }
        DirectoryPicker(
            show = showFilePicker,
            initialDirectory = if (dir.isValid) dir.value else System.getProperty("user.home"),
            onFileSelected = { selectedDir ->
                if (selectedDir != null) {
                    onDirChanged(selectedDir)
                } else {
                    showFilePicker = false
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DirTextField(
    dir: Validatable<String, DirError>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onBrowseClick: () -> Unit,
) {
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
        modifier = modifier,
    ) {
        TextField(
            value = dir.value,
            onValueChange = onValueChange,
            trailingIcon = {
                IconButton(
                    onClick = onBrowseClick,
                ) {
                    Icon(
                        "expui/inline/browse.svg",
                        iconClass = AllIcons::class.java,
                        contentDescription = "Browse",
                    )
                }
            },
            outline = if (dir.isValid) Outline.None else Outline.Error,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StringPropertyRow(
    propertyName: String,
    value: Validatable<String, Unit>,
    onValueChanged: (String) -> Unit,
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
                        Bundle.messagePointer("generator_property_invalid_empty_message", propertyName)
                            .get(),
                    )
                }
            },
            modifier = Modifier.weight(WEIGHT_RIGHT),
        ) {
            TextField(
                value = value.value,
                onValueChange = onValueChanged,
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
