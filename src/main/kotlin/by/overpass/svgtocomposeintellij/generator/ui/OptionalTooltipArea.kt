package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun OptionalTooltipArea(
    tooltip: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    showDelay: Duration = 500.milliseconds,
    tooltipPlacement: TooltipPlacement = TooltipPlacement.CursorPoint(
        offset = DpOffset(0.dp, 16.dp),
    ),
    content: @Composable () -> Unit,
) {
    var parentBounds by remember { mutableStateOf(Rect.Zero) }
    var popupPosition by remember { mutableStateOf(Offset.Zero) }
    var cursorPosition by remember { mutableStateOf(Offset.Zero) }
    var isHovered by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isHovered) {
        if (isHovered) {
            delay(showDelay)
        }
        isVisible = isHovered
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates -> parentBounds = coordinates.boundsInWindow() }
            .onPointerEvent(PointerEventType.Enter) { event ->
                cursorPosition = event.position
                isHovered = true
            }
            .onPointerEvent(PointerEventType.Move) { event ->
                cursorPosition = event.position
                isHovered = isHovered && parentBounds.topLeft + event.position in parentBounds
            }
            .onPointerEvent(PointerEventType.Exit) { event ->
                isHovered = isHovered && parentBounds.topLeft + event.position in parentBounds
            }
            .pointerInput(Unit) {
                detectDown {
                    isHovered = false
                }
            },
    ) {
        content()
        if (isVisible && tooltip != null) {
            Popup(
                popupPositionProvider = tooltipPlacement.positionProvider(cursorPosition),
                onDismissRequest = { isHovered = false },
            ) {
                Box(
                    Modifier
                        .onGloballyPositioned { coordinates -> popupPosition = coordinates.positionInWindow() }
                        .onPointerEvent(PointerEventType.Move) { event ->
                            isHovered = isHovered && popupPosition + event.position in parentBounds
                        }
                        .onPointerEvent(PointerEventType.Exit) { event ->
                            isHovered = isHovered && popupPosition + event.position in parentBounds
                        },
                ) {
                    tooltip()
                }
            }
        }
    }
}
