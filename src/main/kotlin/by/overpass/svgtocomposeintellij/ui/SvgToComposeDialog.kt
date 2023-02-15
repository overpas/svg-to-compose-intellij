package by.overpass.svgtocomposeintellij.ui

import br.com.devsrsouza.svg2compose.VectorType
import by.overpass.svgtocomposeintellij.presentation.SvgToComposeWizardViewModel
import by.overpass.svgtocomposeintellij.presentation.validation.ValidationResult
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.CellBuilder
import com.intellij.ui.layout.Row
import com.intellij.ui.layout.panel
import com.intellij.ui.layout.toNullable
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.event.DocumentEvent
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty0
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SvgToComposeDialog(
    project: Project,
    private val model: SvgToComposeWizardViewModel,
) : DialogWrapper(project, false) {

    private val coroutineScope = CoroutineScope(SupervisorJob())

    private val ui = panel {
        row("Accessor name") {
            stringField(model::accessorName).withValidator(JBTextField::getText, model::validateAccessorName)
        }
        row {
            comment(
                "Will be used to access the Vector in the code like `MyIconPack.IconName` " +
                        "or `MyIconPack.IconGroupDir.IconName`"
            )
        }
        row("Output directory") {
            chooseFolderField(model.stubFile, model::outputDir)
                .withValidator(
                    { File(text) },
                    model::validateOutputDir,
                )
        }
        row("Vector images directory") {
            chooseFolderField(model.stubFile, model::vectorsDir)
                .withValidator(
                    { File(text) },
                    model::validateVectorsDir,
                )
        }
        row("Vector image type") {
            vectorTypeComboBox(model::vectorImageType)
        }
        row("All assets property name") {
            stringField(model::allAssetsPropertyName)
                .withValidator(JBTextField::getText, model::validateAllAssetsPropertyName)
        }
    }

    private val action = CreateAction("Create")

    init {
        init()
        coroutineScope.launch {
            model.isInputValid.collect { isValid ->
                action.isEnabled = isValid
            }
        }
        @Suppress("DialogTitleCapitalization")
        title = "Create Compose Icons from SVG or Android vector drawables"
    }

    override fun createCenterPanel(): JComponent = ui

    override fun getOKAction(): Action = action

    override fun dispose() {
        super.dispose()
        model.handleDisposed()
        coroutineScope.cancel()
    }

    private inner class CreateAction(name: String) : DialogWrapperAction(name) {

        override fun doAction(e: ActionEvent?) {
            model.handleCreateClick()
            close(OK_EXIT_CODE, true)
        }
    }
}

fun Row.stringField(prop: KMutableProperty0<String>): CellBuilder<JBTextField> {
    return textField(prop).apply {
        component.document.addDocumentListener(
            object : DocumentAdapter() {
                override fun textChanged(documentEvent: DocumentEvent) {
                    val text = documentEvent.document.getText(0, documentEvent.document.length)
                    prop.set(text)
                }
            }
        )
    }
}

fun Row.chooseFolderField(
    stubFile: File,
    prop: KMutableProperty<File>,
): CellBuilder<TextFieldWithBrowseButton> {
    return textFieldWithBrowseButton(
        fileChooserDescriptor = fileChooserDescriptor(chooseFolders = true),
    ) { virtualFile ->
        prop.setter.call(File(virtualFile.path))
        virtualFile.path
    }.apply {
        val value = prop.getter.call()
        if (value != stubFile) {
            component.text = value.path
        }
    }
}

fun fileChooserDescriptor(
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

private fun <T : JComponent, V> CellBuilder<T>.withValidator(
    getValue: T.() -> V,
    validator: (V) -> ValidationResult<String>,
): CellBuilder<T> = withValidationOnInput {
    when (val result = validator(it.getValue())) {
        is ValidationResult.Ok -> null
        is ValidationResult.Error -> error(result.value)
    }
}

private fun Row.vectorTypeComboBox(prop: KMutableProperty<VectorType>): CellBuilder<ComboBox<VectorType>> {
    val comboBoxModel = EnumComboBoxModel(VectorType::class.java)
    comboBoxModel.onSelected {
        prop.setter.call(it)
    }
    return comboBox(comboBoxModel, prop.toBinding().toNullable())
}
