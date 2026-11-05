package by.overpass.svgtocomposeintellij.generator.domain

import java.io.File

fun interface SourceRootResolver {

    fun resolve(dir: File): File?
}
