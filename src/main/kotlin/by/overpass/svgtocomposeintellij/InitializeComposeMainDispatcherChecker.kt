package by.overpass.svgtocomposeintellij

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * Workaround
 * https://github.com/JetBrains/jewel/issues/739#issuecomment-2566454062
 * https://youtrack.jetbrains.com/issue/IJPL-166436/Freeze-in-AWTEventQueue-due-to-MainDispatcherChecker
 */
fun initializeComposeMainDispatcherChecker() {
    object : LifecycleOwner {

        override val lifecycle = LifecycleRegistry(this)

        init {
            lifecycle.currentState = Lifecycle.State.STARTED
        }
    }
}
