package by.overpass.svgtocomposeintellij

import br.com.devsrsouza.svg2compose.VectorType
import com.android.tools.adtui.validation.ValidatorPanel
import com.android.tools.idea.observable.core.ObjectValueProperty
import com.android.tools.idea.observable.core.ObservableBool
import com.android.tools.idea.observable.core.StringProperty
import com.android.tools.idea.wizard.model.ModelWizardStep
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.CellBuilder
import com.intellij.ui.layout.Row
import com.intellij.ui.layout.panel
import org.jetbrains.kotlin.idea.core.util.onTextChange
import java.io.File
import javax.swing.JComponent
import javax.swing.JSpinner
import javax.swing.SpinnerListModel

class SvgToComposeWizardStep(
    model: SvgToComposeWizardModel,
    title: String,
) : ModelWizardStep<SvgToComposeWizardModel>(model, title) {

    private val validatorPanel: ValidatorPanel by lazy {
        ValidatorPanel(this, component).apply {
            registerValidator(
                model.applicationIconPackage,
                notEmptyStringValidator("Application icon package"),
            )
            registerValidator(
                model.accessorName,
                notEmptyStringValidator("Accessor name"),
            )
            registerValidator(
                model.allAssetsPropertyName,
                notEmptyStringValidator("All assets property name"),
            )
            registerValidator(
                model.outputDir,
                nonEqualValidator(model.stubFile, "Choose a different file"),
            )
            registerValidator(
                model.vectorsDir,
                nonEqualValidator(model.stubFile, "Choose a different file"),
            )
        }
    }

    override fun getComponent(): JComponent = panel {
        row {
            label("Application icon package")
            stringField(model.applicationIconPackage)
        }
        row {
            comment(
                "Represents what will be the final package of the generated Vector Source." +
                        " ex com.yourcompany.yourapplication.icons"
            )
        }
        row {
            label("Accessor name")
            stringField(model.accessorName)
        }
        row {
            comment(
                "Will be used to access the Vector in the code like `MyIconPack.IconName` " +
                        "or `MyIconPack.IconGroupDir.IconName`"
            )
        }
        row {
            label("Output directory")
            chooseFileField(model.stubFile, model.outputDir)
        }
        row {
            label("Vector images directory")
            chooseFileField(model.stubFile, model.vectorsDir)
        }
        row {
            label("Vector image type")
            vectorTypeSpinner(model.vectorImageType, VectorType.values())
        }
        row {
            label("All assets property name")
            stringField(model.allAssetsPropertyName)
        }
    }

    override fun canGoForward(): ObservableBool = validatorPanel.hasErrors().not()
}

private fun Row.stringField(prop: StringProperty): CellBuilder<JBTextField> {
    val textField = JBTextField()
    textField.text = prop.get()
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

private fun Row.chooseFileField(
    stubFile: File,
    prop: ObjectValueProperty<File>
): CellBuilder<TextFieldWithBrowseButton> {
    return textFieldWithBrowseButton(
        value = if (prop.get() != stubFile) {
            prop.get().path
        } else {
            null
        },
        fileChooserDescriptor = fileChooserDescriptor(chooseFolders = true),
    ) { virtualFile ->
        prop.set(File(virtualFile.path))
        virtualFile.path
    }
}

private fun fileChooserDescriptor(
    chooseFiles: Boolean = false,
    chooseFolders: Boolean = false,
    chooseJars: Boolean = false,
    chooseJarsAsFiles: Boolean = false,
    chooseJarContents: Boolean = false,
    chooseMultiple: Boolean = false,
): FileChooserDescriptor = FileChooserDescriptor(
    chooseFiles,
    chooseFolders,
    chooseJars,
    chooseJarsAsFiles,
    chooseJarContents,
    chooseMultiple,
)

private fun Row.vectorTypeSpinner(
    prop: ObjectValueProperty<VectorType>,
    vectorTypes: Array<VectorType>
): CellBuilder<JSpinner> {
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