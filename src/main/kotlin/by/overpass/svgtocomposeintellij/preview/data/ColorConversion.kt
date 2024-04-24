package by.overpass.svgtocomposeintellij.preview.data

@Suppress("MagicNumber")
fun hexToLongColor(hex: String): Long {
    check(hex.startsWith("0x"))
    // Remove any leading '0x' characters
    val hexValue = hex.removePrefix("0x")

    // Parse hex string to separate ARGB components
    val alpha = hexValue.substring(0, 2).toInt(16)
    val red = hexValue.substring(2, 4).toInt(16)
    val green = hexValue.substring(4, 6).toInt(16)
    val blue = hexValue.substring(6, 8).toInt(16)

    // Combine components into a long color value
    val color: Long = (alpha.toLong() shl 24) or (red.toLong() shl 16) or (green.toLong() shl 8) or blue.toLong()

    return color
}
