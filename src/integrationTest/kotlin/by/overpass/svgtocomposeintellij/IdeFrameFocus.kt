package by.overpass.svgtocomposeintellij

import com.intellij.driver.sdk.ui.components.common.IdeaFrameUI
import com.intellij.driver.sdk.waitFor
import kotlin.time.Duration.Companion.seconds

private val FOCUS_TIMEOUT = 30.seconds

fun IdeaFrameUI.requireInputFocus() {
    waitFor("IDE frame owns the input focus, so that robot input cannot reach another application", FOCUS_TIMEOUT) {
        isFocused()
    }
}
