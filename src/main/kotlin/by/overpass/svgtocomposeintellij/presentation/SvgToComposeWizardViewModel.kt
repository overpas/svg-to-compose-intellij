package by.overpass.svgtocomposeintellij.presentation

import br.com.devsrsouza.svg2compose.VectorType
import by.overpass.svgtocomposeintellij.domain.SvgToComposeData
import by.overpass.svgtocomposeintellij.domain.SvgToComposeService
import by.overpass.svgtocomposeintellij.presentation.validation.CantBeEmptyStringValidator
import by.overpass.svgtocomposeintellij.presentation.validation.NonEqualToValueValidator
import by.overpass.svgtocomposeintellij.presentation.validation.ValidationResult
import by.overpass.svgtocomposeintellij.presentation.validation.ValueValidator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.File

class SvgToComposeWizardViewModel(
    private val svgToComposeService: SvgToComposeService,
    targetDir: File?,
) {

    private val coroutineScope = CoroutineScope(SupervisorJob())

    val stubFile = File("")

    var accessorName = "MyIconPack"
    var outputDir = targetDir ?: stubFile
    var vectorsDir = stubFile
    var vectorImageType = VectorType.SVG
    var allAssetsPropertyName = "AllIcons"

    private val accessorNameValidator = CantBeEmptyStringValidator("Accessor name")
    private val allAssetsPropertyNameValidator = CantBeEmptyStringValidator("All assets property name")
    private val outputDirValidator = NonEqualToValueValidator(stubFile, "Choose a proper output directory")
    private val vectorsDirValidator = NonEqualToValueValidator(
        stubFile,
        "Choose a proper Vector images directory",
    )

    private val accessorNameValid = MutableStateFlow(true)
    private val outputDirValid = MutableStateFlow(true)
    private val vectorsDirValid = MutableStateFlow(false)
    private val allAssetsPropertyNameValid = MutableStateFlow(true)

    @Suppress("ArrayPrimitive")
    val isInputValid = combine(
        accessorNameValid,
        outputDirValid,
        vectorsDirValid,
        allAssetsPropertyNameValid,
    ) { values: Array<Boolean> ->
        values.all { valid -> valid }
    }

    fun validateAccessorName(accessorName: String): ValidationResult<String> {
        return accessorName.validateProperty(accessorNameValid, accessorNameValidator)
    }

    fun validateOutputDir(outputDir: File): ValidationResult<String> {
        return outputDir.validateProperty(outputDirValid, outputDirValidator)
    }

    fun validateVectorsDir(vectorsDir: File): ValidationResult<String> {
        return vectorsDir.validateProperty(vectorsDirValid, vectorsDirValidator)
    }

    fun validateAllAssetsPropertyName(allAssetsPropertyName: String): ValidationResult<String> {
        return allAssetsPropertyName.validateProperty(allAssetsPropertyNameValid, allAssetsPropertyNameValidator)
    }

    private fun <T> T.validateProperty(
        propertyValid: MutableSharedFlow<Boolean>,
        validator: ValueValidator<T, String>,
    ): ValidationResult<String> {
        val result = validator.validate(this)
        coroutineScope.launch {
            val valid = result is ValidationResult.Ok
            propertyValid.emit(valid)
        }
        return result
    }

    fun handleCreateClick() {
        svgToComposeService.convertSvgToCompose(
            SvgToComposeData(
                applicationIconPackage = "",
                accessorName = accessorName,
                outputDir = outputDir,
                vectorsDir = vectorsDir,
                vectorImageType = vectorImageType,
                allAssetsPropertyName = allAssetsPropertyName,
            )
        )
    }

    fun handleDisposed() {
        coroutineScope.cancel()
    }
}