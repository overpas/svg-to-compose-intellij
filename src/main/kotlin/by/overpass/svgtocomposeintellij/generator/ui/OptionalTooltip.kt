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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.foundation.theme.OverrideDarkMode
import org.jetbrains.jewel.ui.component.styling.TooltipStyle
import org.jetbrains.jewel.ui.theme.tooltipStyle
import org.jetbrains.jewel.ui.util.isDark

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun OptionalTooltip(
    tooltip: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    style: TooltipStyle = JewelTheme.tooltipStyle,
    tooltipPlacement: TooltipPlacement = style.metrics.placement,
    content: @Composable () -> Unit,
) {
    OptionalTooltipArea(
        tooltip = tooltip?.let { tooltipContent ->
            @Composable {
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
                            tooltipContent()
                        }
                    }
                }
            }
        },
        modifier = modifier,
        showDelay = style.metrics.showDelay,
        tooltipPlacement = tooltipPlacement,
        content = content,
    )
}
