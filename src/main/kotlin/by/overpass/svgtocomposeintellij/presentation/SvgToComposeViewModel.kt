package by.overpass.svgtocomposeintellij.presentation

import by.overpass.svgtocomposeintellij.presentation.validation.DirError
import by.overpass.svgtocomposeintellij.presentation.validation.Validatable
import by.overpass.svgtocomposeintellij.presentation.validation.ValidationResult
import by.overpass.svgtocomposeintellij.presentation.validation.ValueValidator
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class SvgToComposeModel(
    val accessorName: Validatable<String, Unit> = Validatable(value = "MyIconPack"),
    val outputDir: Validatable<String, DirError> = Validatable(value = "", isValid = false),
    val vectorImagesDir: Validatable<String, DirError> = Validatable(value = "", isValid = false),
    val vectorImageType: VectorImageType = VectorImageType.SVG,
    val allAssetsPropertyName: Validatable<String, Unit> = Validatable(value = "AllIcons"),
)

val SvgToComposeModel.isValid: Boolean
    get() = accessorName.isValid && outputDir.isValid && vectorImagesDir.isValid && allAssetsPropertyName.isValid

enum class VectorImageType {
    SVG, DRAWABLE,
}

interface SvgToComposeViewModel {

    val state: StateFlow<SvgToComposeModel>

    fun onAccessorNameChanged(accessorName: String)

    fun onOutputDirChanged(outputDir: String)

    fun onVectorImagesDirChanged(vectorImagesDir: String)

    fun onVectorImageTypeChanged(vectorImageType: VectorImageType)

    fun onAllAssetsPropertyNameChanged(allAssetsPropertyName: String)

    fun generate()
}

class SvgToComposeViewModelImpl(
    targetDir: File,
    private val nonStringEmptyValidator: ValueValidator<String, Unit>,
    private val directoryValidator: ValueValidator<String, DirError>,
) : SvgToComposeViewModel {

    override val state = MutableStateFlow(
        SvgToComposeModel(
            outputDir = Validatable(value = targetDir.path),
        ),
    )

    override fun onAccessorNameChanged(accessorName: String) {
        state.update { old ->
            val validationResult = nonStringEmptyValidator.validate(accessorName)
            old.copy(
                accessorName = old.accessorName.copy(
                    value = accessorName,
                    isValid = validationResult == ValidationResult.Ok,
                    error = when (validationResult) {
                        is ValidationResult.Ok -> null
                        is ValidationResult.Error -> Unit
                    },
                ),
            )
        }
    }

    override fun onOutputDirChanged(outputDir: String) {
        state.update { old ->
            val validationResult = directoryValidator.validate(outputDir)
            old.copy(
                outputDir = old.outputDir.copy(
                    value = outputDir,
                    isValid = validationResult == ValidationResult.Ok,
                    error = when (validationResult) {
                        is ValidationResult.Ok -> null
                        is ValidationResult.Error -> validationResult.error
                    },
                ),
            )
        }
    }

    override fun onVectorImagesDirChanged(vectorImagesDir: String) {
        state.update { old ->
            val validationResult = directoryValidator.validate(vectorImagesDir)
            old.copy(
                vectorImagesDir = old.vectorImagesDir.copy(
                    value = vectorImagesDir,
                    isValid = validationResult == ValidationResult.Ok,
                    error = when (validationResult) {
                        is ValidationResult.Ok -> null
                        is ValidationResult.Error -> validationResult.error
                    },
                ),
            )
        }
    }

    override fun onVectorImageTypeChanged(vectorImageType: VectorImageType) {
        state.update { old ->
            old.copy(vectorImageType = vectorImageType)
        }
    }

    override fun onAllAssetsPropertyNameChanged(allAssetsPropertyName: String) {
        state.update { old ->
            val validationResult = nonStringEmptyValidator.validate(allAssetsPropertyName)
            old.copy(
                allAssetsPropertyName = old.allAssetsPropertyName.copy(
                    value = allAssetsPropertyName,
                    isValid = validationResult == ValidationResult.Ok,
                    error = when (validationResult) {
                        is ValidationResult.Ok -> null
                        is ValidationResult.Error -> Unit
                    },
                ),
            )
        }
    }

    override fun generate() {
        // TODO: Generate
    }
}
