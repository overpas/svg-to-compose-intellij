package by.overpass.svgtocomposeintellij.ui

import org.jdesktop.swingx.combobox.ListComboBoxModel
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class OnSelectedTest {

    private val model = ListComboBoxModel<Unit>(mutableListOf())

    @Test
    fun `test callback triggered when model updated`() {
        val mockOnSelected = mock<(Any) -> Unit>()

        model.onSelected(mockOnSelected)
        model.selectedItem = Unit

        verify(mockOnSelected).invoke(Unit)
    }
}