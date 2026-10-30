package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.generator.domain.VectorImageType
import org.jetbrains.jewel.ui.component.ListComboBox
import org.jetbrains.jewel.ui.component.Text

@Composable
internal fun VectorImageTypeRow(
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
        ListComboBox(
            items = VectorImageType.entries.map { imageType -> imageType.name },
            selectedIndex = vectorImageType.ordinal,
            onSelectedItemChange = { index -> onVectorImageTypeChange(VectorImageType.entries[index]) },
            modifier = Modifier.weight(WEIGHT_RIGHT),
        )
    }
}
