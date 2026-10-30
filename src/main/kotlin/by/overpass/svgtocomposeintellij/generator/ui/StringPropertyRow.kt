package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import by.overpass.svgtocomposeintellij.Bundle
import by.overpass.svgtocomposeintellij.generator.presentation.validation.Validatable
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun StringPropertyRow(
    propertyName: String,
    value: Validatable<String, Unit>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
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
                            propertyName,
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
                    currentOnValueChange(charSequence.toString())
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
