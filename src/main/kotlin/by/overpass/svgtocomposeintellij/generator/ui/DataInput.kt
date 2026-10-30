package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.generator.domain.VectorImageType
import by.overpass.svgtocomposeintellij.generator.presentation.DataInput
import com.intellij.openapi.project.Project
import org.jetbrains.jewel.ui.component.CheckboxRow
import org.jetbrains.jewel.ui.component.Text

@Composable
internal fun DataInput(
    dataInput: DataInput,
    project: Project,
    onAccessorNameChange: (String) -> Unit,
    onOutputDirChange: (String) -> Unit,
    onVectorImagesDirChange: (String) -> Unit,
    onVectorImageTypeChange: (VectorImageType) -> Unit,
    onAllAssetsPropertyNameChange: (String) -> Unit,
    onStringAccessorToggle: (Boolean) -> Unit,
    onPreviewToggle: (Boolean) -> Unit,
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
            project = project,
            onDirChange = onOutputDirChange,
        )
        BrowseDirRow(
            propertyName = Bundle.message("generator_vector_images_directory_hint"),
            dir = dataInput.vectorImagesDir,
            project = project,
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
            checked = dataInput.isStringAccessorGenerated,
            onCheckedChange = onStringAccessorToggle,
        ) {
            Text(Bundle.message("generator_generate_string_accessor_checkbox_text"))
        }
        CheckboxRow(
            checked = dataInput.isPreviewGenerated,
            onCheckedChange = onPreviewToggle,
        ) {
            Text(Bundle.message("generator_generate_preview_checkbox_text"))
        }
    }
}
