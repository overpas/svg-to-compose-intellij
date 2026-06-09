package by.overpass.svgtocomposeintellij.generator.ui

import androidx.compose.runtime.compositionLocalOf
import com.intellij.openapi.project.Project

val LocalProject = compositionLocalOf<Project> { error("Project not provided") }
