package by.overpass.svgtocomposeintellij.preview.presentation

import by.overpass.svgtocomposeintellij.preview.data.IconDataParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface ComposeImageVectorPreviewViewModel {

    val state: StateFlow<ComposeImageVectorPreviewState>

    fun dispatch(action: ComposeImageVectorPreviewIntent)
}

class ComposeImageVectorPreviewViewModelImpl(
    private val coroutineScope: CoroutineScope,
    private val iconDataParser: IconDataParser,
) : ComposeImageVectorPreviewViewModel {

    override val state = MutableStateFlow<ComposeImageVectorPreviewState>(ComposeImageVectorPreviewState.Rendering)

    override fun dispatch(action: ComposeImageVectorPreviewIntent) {
        when (action) {
            is ComposeImageVectorPreviewIntent.ParseFile -> parseIconData()
        }
    }

    private fun parseIconData() {
        coroutineScope.launch {
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
                    }
                )
        }
    }
}
