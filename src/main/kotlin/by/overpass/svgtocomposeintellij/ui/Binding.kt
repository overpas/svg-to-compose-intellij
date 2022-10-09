package by.overpass.svgtocomposeintellij.ui

import com.intellij.ui.layout.PropertyBinding
import kotlin.reflect.KMutableProperty

fun <T> KMutableProperty<T>.toBinding(): PropertyBinding<T> = PropertyBinding(
    get = { getter.call() },
    set = { setter.call(it) },
)