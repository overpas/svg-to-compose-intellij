package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import by.overpass.svgtocomposeintellij.generator.domain.VectorImageType
import by.overpass.svgtocomposeintellij.generator.presentation.DataInput
import com.intellij.openapi.project.Project
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.IndeterminateHorizontalProgressBar

@Composable
internal fun DataInputWithProgress(
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
    ) {
        Dimmed(
            isDimmed = dataInput.isInProgress,
            modifier = Modifier.fillMaxSize().weight(1f),
        ) {
            DataInput(
                dataInput = dataInput,
                project = project,
                onAccessorNameChange = onAccessorNameChange,
                onOutputDirChange = onOutputDirChange,
                onVectorImagesDirChange = onVectorImagesDirChange,
                onVectorImageTypeChange = onVectorImageTypeChange,
                onAllAssetsPropertyNameChange = onAllAssetsPropertyNameChange,
                onStringAccessorToggle = onStringAccessorToggle,
                onPreviewToggle = onPreviewToggle,
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
                modifier = Modifier
                    .fillMaxSize()
                    .background(JewelTheme.globalColors.panelBackground.copy(alpha = 0.5F))
                    .disableClickAndRipple(),
            )
        }
    }
}
