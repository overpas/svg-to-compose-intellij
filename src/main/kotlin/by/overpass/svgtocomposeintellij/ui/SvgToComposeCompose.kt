package by.overpass.svgtocomposeintellij.ui

import javax.swing.JComponent
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.bridge.theme.SwingBridgeTheme
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.component.Text

@OptIn(ExperimentalJewelApi::class)
fun myPluginPanel(): JComponent = JewelComposePanel {
    SwingBridgeTheme {
        Text("Hello Swing!")
    }
}
