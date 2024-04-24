package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.foundation.theme.OverrideDarkMode
import org.jetbrains.jewel.ui.component.styling.TooltipStyle
import org.jetbrains.jewel.ui.theme.tooltipStyle
import org.jetbrains.jewel.ui.util.isDark
import org.jetbrains.jewel.ui.component.TooltipPlacement as JewelTooltipPlacement

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OptionalTooltip(
    tooltip: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    style: TooltipStyle = JewelTheme.tooltipStyle,
    tooltipPlacement: TooltipPlacement = JewelTooltipPlacement(
        contentOffset = style.metrics.tooltipOffset,
        alignment = style.metrics.tooltipAlignment,
        density = LocalDensity.current,
    ),
    content: @Composable () -> Unit,
) {
    OptionalTooltipArea(
        tooltip = if (tooltip != null) {
            {
                CompositionLocalProvider(
                    LocalContentColor provides style.colors.content,
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = style.metrics.shadowSize,
                                shape = RoundedCornerShape(style.metrics.cornerSize),
                                ambientColor = style.colors.shadow,
                                spotColor = Color.Transparent,
                            )
                            .background(
                                color = style.colors.background,
                                shape = RoundedCornerShape(style.metrics.cornerSize),
                            )
                            .border(
                                width = style.metrics.borderWidth,
                                color = style.colors.border,
                                shape = RoundedCornerShape(style.metrics.cornerSize),
                            )
                            .padding(style.metrics.contentPadding),
                    ) {
                        OverrideDarkMode(style.colors.background.isDark()) {
                            tooltip()
                        }
                    }
                }
            }
        } else null,
        modifier = modifier,
        delayMillis = style.metrics.showDelay.inWholeMilliseconds.toInt(),
        tooltipPlacement = tooltipPlacement,
        content = content,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OptionalTooltipArea(
    tooltip: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    delayMillis: Int = 500,
    tooltipPlacement: TooltipPlacement = TooltipPlacement.CursorPoint(
        offset = DpOffset(0.dp, 16.dp)
    ),
    content: @Composable () -> Unit
) {
    var parentBounds by remember { mutableStateOf(Rect.Zero) }
    var popupPosition by remember { mutableStateOf(Offset.Zero) }
    var cursorPosition by remember { mutableStateOf(Offset.Zero) }
    var isVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var job: Job? by remember { mutableStateOf(null) }

    fun startShowing() {
        job?.cancel()
        job = scope.launch {
            delay(delayMillis.toLong())
            isVisible = true
        }
    }

    fun hide() {
        job?.cancel()
        isVisible = false
    }

    fun hideIfNotHovered(globalPosition: Offset) {
        if (!parentBounds.contains(globalPosition)) {
            hide()
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { parentBounds = it.boundsInWindow() }
            .onPointerEvent(PointerEventType.Enter) {
                cursorPosition = it.position
                startShowing()
            }
            .onPointerEvent(PointerEventType.Move) {
                cursorPosition = it.position
                hideIfNotHovered(parentBounds.topLeft + it.position)
            }
            .onPointerEvent(PointerEventType.Exit) {
                hideIfNotHovered(parentBounds.topLeft + it.position)
            }
            .pointerInput(Unit) {
                detectDown {
                    hide()
                }
            }
    ) {
        content()
        if (isVisible && tooltip != null) {
            @OptIn(ExperimentalFoundationApi::class)
            Popup(
                popupPositionProvider = tooltipPlacement.positionProvider(cursorPosition),
                onDismissRequest = { isVisible = false }
            ) {
                Box(
                    Modifier
                        .onGloballyPositioned { popupPosition = it.positionInWindow() }
                        .onPointerEvent(PointerEventType.Move) {
                            hideIfNotHovered(popupPosition + it.position)
                        }
                        .onPointerEvent(PointerEventType.Exit) {
                            hideIfNotHovered(popupPosition + it.position)
                        }
                ) {
                    tooltip()
                }
            }
        }
    }
}

private fun Modifier.onPointerEvent(
    eventType: PointerEventType,
    pass: PointerEventPass = PointerEventPass.Main,
    onEvent: AwaitPointerEventScope.(event: PointerEvent) -> Unit
) = pointerInput(eventType, pass, onEvent) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(pass)
            if (event.type == eventType) {
                onEvent(event)
            }
        }
    }
}

private val PointerEvent.position get() = changes.first().position

private suspend fun PointerInputScope.detectDown(onDown: (Offset) -> Unit) {
    while (true) {
        awaitPointerEventScope {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val down = event.changes.find { it.changedToDown() }
            if (down != null) {
                onDown(down.position)
            }
        }
    }
}
