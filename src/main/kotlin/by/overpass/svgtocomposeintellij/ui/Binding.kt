package by.overpass.svgtocomposeintellij.ui

import com.android.tools.idea.observable.core.ObjectValueProperty
import com.android.tools.idea.observable.core.StringProperty
import com.intellij.ui.layout.PropertyBinding

fun StringProperty.toBinding(): PropertyBinding<String> = PropertyBinding(
    get = { get() },
    set = { set(it) },
)

fun <T> ObjectValueProperty<T>.toBinding(): PropertyBinding<T> = PropertyBinding(
    get = { get() },
    set = { set(it) },
)