package by.overpass.svgtocomposeintellij.domain

import com.intellij.openapi.progress.Task

interface SvgToComposeTaskFactory {

    fun createTask(data: SvgToComposeData): Task.Backgroundable
}