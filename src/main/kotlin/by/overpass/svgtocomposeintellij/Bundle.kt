package by.overpass.svgtocomposeintellij

import com.intellij.DynamicBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.function.Supplier

@NonNls
private const val BUNDLE = "messages.Bundle"

object Bundle : DynamicBundle(BUNDLE) {

    @JvmStatic
    fun message(
        @PropertyKey(resourceBundle = BUNDLE) key: String,
        vararg params: Any,
    ): String =
        getMessage(key, *params)

    @JvmStatic
    fun messagePointer(
        @PropertyKey(resourceBundle = BUNDLE) key: String,
        vararg params: Any,
    ): Supplier<String> =
        getLazyMessage(key, *params)
}
