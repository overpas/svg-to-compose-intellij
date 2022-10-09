package by.overpass.svgtocomposeintellij.data

import by.overpass.svgtocomposeintellij.domain.SvgToComposeData
import by.overpass.svgtocomposeintellij.domain.SvgToComposeDataProcessor
import by.overpass.svgtocomposeintellij.domain.SvgToComposeTaskFactory
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project

class SvgToComposeTaskFactoryImpl(
    private val project: Project,
    private val svgToComposeDataProcessor: SvgToComposeDataProcessor,
) : SvgToComposeTaskFactory {

    override fun createTask(data: SvgToComposeData): Task.Backgroundable =
        SvgToComposeTask(project, svgToComposeDataProcessor(data))
}