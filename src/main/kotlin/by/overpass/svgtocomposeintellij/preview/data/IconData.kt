package by.overpass.svgtocomposeintellij.preview.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class IconData(
    val name: String,
    val imageVector: ImageVector,
)
