package by.overpass.svgtocomposeintellij.ui

import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import javax.swing.ComboBoxModel
import javax.swing.event.ListDataEvent

class OnSelectionChangedListenerTest {

    private val mockModel = mock<ComboBoxModel<Any>>()
    private val mockOnSelectionChanged = mock<(Any) -> Unit>()
    private val mockEvent = mock<ListDataEvent>()

    private val listener = OnSelectionChangedListener(mockModel, mockOnSelectionChanged)


    @Test
    fun `test callback not triggered when interval added`() {
        listener.intervalAdded(mockEvent)

        verifyNoInteractions(mockOnSelectionChanged)
    }

    @Test
    fun `test callback not triggered when interval removed`() {
        listener.intervalRemoved(mockEvent)

        verifyNoInteractions(mockOnSelectionChanged)
    }

    @Test
    fun `test callback triggered when contents changed`() {
        whenever(mockModel.selectedItem).thenReturn(Unit)

        listener.contentsChanged(mockEvent)

        verify(mockOnSelectionChanged).invoke(Unit)
    }
}