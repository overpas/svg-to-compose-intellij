package by.overpass.svgtocomposeintellij.presentation

import br.com.devsrsouza.svg2compose.VectorType
import by.overpass.svgtocomposeintellij.domain.SvgToComposeData
import by.overpass.svgtocomposeintellij.domain.SvgToComposeService
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.io.File

class SvgToComposeWizardViewModelTest {

    private val service: SvgToComposeService = mock()
    private val file: File = File("")

    private val viewModel = SvgToComposeWizardViewModel(service, file)

    @Test
    fun `test svg to compose service triggered when create clicked`() {
        viewModel.outputDir = file
        viewModel.vectorsDir = file

        viewModel.handleCreateClick()

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