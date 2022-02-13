package by.overpass.svgtocomposeintellij.presentation

import br.com.devsrsouza.svg2compose.VectorType
import by.overpass.svgtocomposeintellij.data.SvgToComposeData
import by.overpass.svgtocomposeintellij.data.SvgToComposeService
import com.android.tools.idea.observable.core.ObjectValueProperty
import com.android.tools.idea.observable.core.StringValueProperty
import com.android.tools.idea.wizard.model.WizardModel
import java.io.File

class SvgToComposeWizardViewModel(
    private val svgToComposeService: SvgToComposeService,
    targetDir: File?,
) : WizardModel() {

    val stubFile = File("")

    val applicationIconPackage = StringValueProperty("icons")
    val accessorName = StringValueProperty("MyIconPack")
    val outputDir = ObjectValueProperty<File>(targetDir ?: stubFile)
    val vectorsDir = ObjectValueProperty<File>(stubFile)
    val vectorImageType = ObjectValueProperty<VectorType>(VectorType.SVG)
    val allAssetsPropertyName = StringValueProperty("AllIcons")

    override fun handleFinished() {
        svgToComposeService.convertSvgToCompose(
            SvgToComposeData(
                applicationIconPackage = applicationIconPackage.get(),
                accessorName = accessorName.get(),
                outputDir = outputDir.get(),
                vectorsDir = vectorsDir.get(),
                vectorImageType = vectorImageType.get(),
                allAssetsPropertyName = allAssetsPropertyName.get(),
            )
        )
    }
}