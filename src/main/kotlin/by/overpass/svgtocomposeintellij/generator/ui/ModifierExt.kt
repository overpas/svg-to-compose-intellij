package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

fun Modifier.disableClickAndRipple(): Modifier =
    clickable(
        enabled = false,
        indication = null,
        interactionSource = null,
        onClick = { },
    )
