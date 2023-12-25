package by.overpass.svgtocomposeintellij.ui

import androidx.compose.foundation.ExperimentalFoundationApi
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
import by.overpass.svgtocomposeintellij.presentation.SvgToComposeViewModel
import by.overpass.svgtocomposeintellij.presentation.VectorImageType
import by.overpass.svgtocomposeintellij.presentation.validation.DirError
import by.overpass.svgtocomposeintellij.presentation.validation.Validatable
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.intellij.icons.AllIcons
import javax.swing.JComponent
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

private const val WEIGHT_LEFT = 1f
private const val WEIGHT_RIGHT = 3f

@OptIn(ExperimentalJewelApi::class)
fun svgToComposePluginPanel(viewModel: SvgToComposeViewModel): JComponent = JewelComposePanel {
    SwingBridgeTheme {
        SvgToComposePlugin(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun SvgToComposePlugin(viewModel: SvgToComposeViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    Column(modifier) {
        Text(
            text = "Accessor name will be used to access the Vector in the code like `MyIconPack.IconName` or " +
                    "`MyIconPack.IconGroupDir.IconName`",
        )
        Spacer(Modifier.height(8.dp))
        StringPropertyRow(
            propertyName = "Accessor name",
            value = state.accessorName,
            onValueChanged = viewModel::onAccessorNameChanged,
        )
        Spacer(Modifier.height(8.dp))
        BrowseDirRow(
            propertyName = "Output directory",
            dir = state.outputDir,
            onDirChanged = viewModel::onOutputDirChanged,
        )
        Spacer(Modifier.height(8.dp))
        BrowseDirRow(
            propertyName = "Vector images directory:",
            dir = state.vectorImagesDir,
            onDirChanged = viewModel::onVectorImagesDirChanged,
        )
        Spacer(Modifier.height(8.dp))
        VectorImageTypeRow(
            vectorImageType = state.vectorImageType,
            onVectorImageTypeChanded = viewModel::onVectorImageTypeChanged,
        )
        Spacer(Modifier.height(8.dp))
        StringPropertyRow(
            propertyName = "All assets property name",
            value = state.allAssetsPropertyName,
            onValueChanged = viewModel::onAllAssetsPropertyNameChanged,
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
            text = "Vector image type:",
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
                { Text("Choose a proper output directory") }
            }
            is DirError.InvalidPath -> {
                { Text("Invalid directory") }
            }
            is DirError.NotADirectory -> {
                { Text("The chosen path is not a directory") }
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
                { Text("$propertyName can't be empty") }
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
