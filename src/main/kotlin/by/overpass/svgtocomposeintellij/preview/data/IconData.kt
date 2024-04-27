package by.overpass.svgtocomposeintellij.preview.data

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
data class IconData(
    val name: String,
    val imageVector: ImageVector,
)
