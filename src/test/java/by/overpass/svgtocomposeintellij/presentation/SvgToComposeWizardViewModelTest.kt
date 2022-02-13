package by.overpass.svgtocomposeintellij.presentation

import br.com.devsrsouza.svg2compose.VectorType
import by.overpass.svgtocomposeintellij.data.SvgToComposeData
import by.overpass.svgtocomposeintellij.data.SvgToComposeService
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.io.File

class SvgToComposeWizardViewModelTest {

    private val service: SvgToComposeService = mock()
    private val file: File = File("")

    private val viewModel = SvgToComposeWizardViewModel(service, file)

    @Test
    fun `test svg to compose service triggered when finish clicked`() {
        viewModel.outputDir.set(file)
        viewModel.vectorsDir.set(file)

        viewModel.handleFinished()

        verify(service).convertSvgToCompose(
            SvgToComposeData(
                applicationIconPackage = "",
                "MyIconPack",
                outputDir = file,
                vectorsDir = file,
                vectorImageType = VectorType.SVG,
                allAssetsPropertyName = "AllIcons"
            )
        )
    }
}