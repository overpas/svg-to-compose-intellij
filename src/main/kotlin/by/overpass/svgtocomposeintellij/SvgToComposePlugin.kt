package by.overpass.svgtocomposeintellij

import br.com.devsrsouza.svg2compose.Svg2Compose
import br.com.devsrsouza.svg2compose.VectorType
import com.android.tools.idea.ui.wizard.StudioWizardDialogBuilder
import com.android.tools.idea.wizard.model.ModelWizard
import com.android.tools.idea.wizard.model.ModelWizardStep
import com.android.tools.idea.wizard.model.WizardModel
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.CellBuilder
import com.intellij.ui.layout.Row
import com.intellij.ui.layout.panel
import com.intellij.ui.layout.toBinding
import com.intellij.util.ui.JBUI
import org.jetbrains.kotlin.idea.core.util.onTextChange
import java.io.File
import javax.swing.JComponent
import javax.swing.JSpinner
import javax.swing.SpinnerListModel
import kotlin.reflect.KMutableProperty0

class SvgToComposeAction : AnAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = icon
    }

    override fun actionPerformed(event: AnActionEvent) {
        StudioWizardDialogBuilder(
            ModelWizard.Builder()
                .addStep(
                    SvgToComposeWizardStep(
                        SvgToComposeWizardModel(),
                        "Create Compose Icons from SVG or Android vector drawables",
                    )
                )
                .build(),
            "SVG to Compose",
        )
            .setProject(event.project)
            .setMinimumSize(JBUI.size(800, 600))
            .setPreferredSize(JBUI.size(1020, 680))
            .build()
            .show()
    }

    companion object {
        val icon = IconLoader.getIcon("/icons/ic_compose.png", SvgToComposeAction::class.java)
    }
}

class SvgToComposeWizardModel : WizardModel() {

    var applicationIconPackage: String = ""
    var accessorName: String = ""
    var outputDir: File? = null
    var vectorsDir: File? = null
    var vectorImageType: VectorType = VectorType.SVG
    var allAssetsPropertyName: String = ""

    override fun handleFinished() {
        if (isValid()) {
            Svg2Compose.parse(
                applicationIconPackage = applicationIconPackage,
                accessorName = accessorName,
                outputSourceDirectory = outputDir!!,
                vectorsDirectory = vectorsDir!!,
                type = vectorImageType,
                allAssetsPropertyName = allAssetsPropertyName,
            )
        }
    }

    fun isValid(): Boolean = applicationIconPackage.isNotEmpty()
            && accessorName.isNotEmpty()
            && outputDir != null
            && vectorsDir != null
            && allAssetsPropertyName.isNotEmpty()
}


class SvgToComposeWizardStep(
    model: SvgToComposeWizardModel,
    title: String,
) : ModelWizardStep<SvgToComposeWizardModel>(model, title) {

    override fun getComponent(): JComponent = panel {
        commentRow(
            "Represents what will be the final package of the generated Vector Source." +
                    " ex com.yourcompany.yourapplication.icons"
        )
        row {
            label("Application icon package")
            stringTextField(model::applicationIconPackage)
        }
        commentRow(
            "Will be used to access the Vector in the code like `MyIconPack.IconName` " +
                    "or `MyIconPack.IconGroupDir.IconName`"
        )
        row {
            label("Accessor name")
            stringTextField(model::accessorName)
        }
        row {
            label("Output directory")
            textFieldWithBrowseButton(
                fileChooserDescriptor = FileChooserDescriptor(
                    false,
                    true,
                    false,
                    false,
                    false,
                    false,
                )
            ) {
                model.outputDir = File(it.path)
                it.path
            }
        }
        row {
            label("Vector images directory")
            textFieldWithBrowseButton(
                fileChooserDescriptor = FileChooserDescriptor(
                    false,
                    true,
                    false,
                    false,
                    false,
                    false,
                )
            ) {
                model.vectorsDir = File(it.path)
                it.path
            }
        }
        row {
            label("Vector image type")
            vectorTypeSpinner(model::vectorImageType, VectorType.values())
        }
        row {
            label("All assets property name")
            stringTextField(model::allAssetsPropertyName)
        }
    }
}

fun Row.vectorTypeSpinner(prop: KMutableProperty0<VectorType>, vectorTypes: Array<VectorType>): CellBuilder<JSpinner> {
    val jSpinner = JSpinner()
    jSpinner.model = SpinnerListModel(vectorTypes)
    return component(jSpinner).withBinding(
        { spinner ->
            spinner.value as VectorType
        },
        { spinner, vectorType ->
            spinner.value = vectorType
        },
        prop.toBinding(),
    )
}

fun Row.stringTextField(prop: KMutableProperty0<String>): CellBuilder<JBTextField> {
    val textField = JBTextField()
    textField.onTextChange { documentEvent ->
        val text = documentEvent.document.getText(0, documentEvent.document.length)
        prop.set(text)
    }
    return component(textField).withBinding(
        {
            it.text
        },
        { view, text ->
            view.text = text
        },
        prop.toBinding(),
    )
}
