package by.overpass.svgtocomposeintellij.data

import com.intellij.openapi.project.Project

interface SvgToComposeTaskFactory {

    fun createTask(data: SvgToComposeData): SvgToComposeTask

    companion object {
        operator fun invoke(
            project: Project,
            svgToComposeDataProcessor: SvgToComposeDataProcessor,
        ): SvgToComposeTaskFactory = SvgToComposeTaskFactoryImpl(project, svgToComposeDataProcessor)
    }
}

class SvgToComposeTaskFactoryImpl(
    private val project: Project,
    private val svgToComposeDataProcessor: SvgToComposeDataProcessor,
) : SvgToComposeTaskFactory {

    override fun createTask(data: SvgToComposeData): SvgToComposeTask =
        SvgToComposeTask(project, svgToComposeDataProcessor(data))
}