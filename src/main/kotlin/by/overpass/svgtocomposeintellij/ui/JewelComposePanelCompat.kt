package by.overpass.svgtocomposeintellij.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposePanel
import javax.swing.JComponent

private val jewelComposePanelMethod by lazy {
    Class.forName("org.jetbrains.jewel.bridge.JewelComposePanelWrapperKt")
        .getMethod(
            "JewelComposePanel",
            Function1::class.java,
            Function2::class.java,
        )
}

internal fun jewelComposePanelCompat(
    config: ComposePanel.() -> Unit = {},
    content: @Composable () -> Unit,
): JComponent {
    return jewelComposePanelMethod.invoke(null, config, content) as JComponent
}
