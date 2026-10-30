package by.overpass.svgtocomposeintellij.preview.presentation

import by.overpass.svgtocomposeintellij.preview.data.IconDataParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

interface ComposeImageVectorPreviewViewModel {

    val state: StateFlow<ComposeImageVectorPreviewState>

    suspend fun dispatch(action: ComposeImageVectorPreviewIntent)
}

class ComposeImageVectorPreviewViewModelImpl(private val iconDataParser: IconDataParser) :
    ComposeImageVectorPreviewViewModel {

    override val state = MutableStateFlow<ComposeImageVectorPreviewState>(ComposeImageVectorPreviewState.Rendering)

    override suspend fun dispatch(action: ComposeImageVectorPreviewIntent) {
        when (action) {
            is ComposeImageVectorPreviewIntent.ParseFile -> parseIconData()
        }
    }

    private suspend fun parseIconData() {
        state.update { ComposeImageVectorPreviewState.Rendering }
        iconDataParser.parse()
            .fold(
                onSuccess = { iconData ->
                    state.update {
                        ComposeImageVectorPreviewState.Icon(iconData)
                    }
                },
                onFailure = {
                    state.update {
                        ComposeImageVectorPreviewState.Error
                    }
                },
            )
    }
}
