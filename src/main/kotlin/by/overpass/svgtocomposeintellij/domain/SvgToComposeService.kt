package by.overpass.svgtocomposeintellij.domain

import com.intellij.openapi.progress.ProgressManager

interface SvgToComposeService {

    fun convertSvgToCompose(data: SvgToComposeData)

    companion object {

        operator fun invoke(
            progressManager: ProgressManager,
            svgToComposeTaskFactory: SvgToComposeTaskFactory,
        ): SvgToComposeService = SvgToComposeServiceImpl(progressManager, svgToComposeTaskFactory)
    }
}

private class SvgToComposeServiceImpl(
    private val progressManager: ProgressManager,
    private val svgToComposeTaskFactory: SvgToComposeTaskFactory,
) : SvgToComposeService {

    override fun convertSvgToCompose(data: SvgToComposeData) {
        progressManager.run(svgToComposeTaskFactory.createTask(data))
    }
}