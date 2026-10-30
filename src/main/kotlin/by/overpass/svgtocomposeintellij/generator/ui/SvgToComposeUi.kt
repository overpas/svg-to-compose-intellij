package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import by.overpass.svgtocomposeintellij.generator.presentation.DataInput
import by.overpass.svgtocomposeintellij.generator.presentation.Error
import by.overpass.svgtocomposeintellij.generator.presentation.Finished
import by.overpass.svgtocomposeintellij.generator.presentation.SvgToComposeViewModel
import com.intellij.openapi.project.Project

internal const val WEIGHT_LEFT = 1f
internal const val WEIGHT_RIGHT = 3f

@Composable
fun SvgToComposePlugin(
    viewModel: SvgToComposeViewModel,
    project: Project,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    when (val currentState = state) {
        is DataInput -> DataInputWithProgress(
            dataInput = currentState,
            project = project,
            onAccessorNameChange = viewModel::onAccessorNameChanged,
            onOutputDirChange = viewModel::onOutputDirChanged,
            onVectorImagesDirChange = viewModel::onVectorImagesDirChanged,
            onVectorImageTypeChange = viewModel::onVectorImageTypeChanged,
            onAllAssetsPropertyNameChange = viewModel::onAllAssetsPropertyNameChanged,
            onStringAccessorToggle = viewModel::onStringAccessorToggled,
            onPreviewToggle = viewModel::onPreviewToggled,
            modifier = modifier,
        )

        is Error -> GenerationError(
            error = currentState,
            modifier = modifier,
        )

        is Finished -> Unit
    }
}
