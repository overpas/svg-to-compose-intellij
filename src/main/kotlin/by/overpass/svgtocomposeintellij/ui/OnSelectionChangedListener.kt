package by.overpass.svgtocomposeintellij.ui

import javax.swing.ComboBoxModel
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class OnSelectionChangedListener<T>(
    private val model: ComboBoxModel<T>,
    private val onSelectionChanged: (T) -> Unit,
) : ListDataListener {

    override fun intervalAdded(e: ListDataEvent?) {
        // do nothing
    }

    override fun intervalRemoved(e: ListDataEvent?) {
        // do nothing
    }

    override fun contentsChanged(e: ListDataEvent?) {
        onSelectionChanged(model.selectedItem as T)
    }
}

fun <T> ComboBoxModel<T>.onSelected(onSelected: (T) -> Unit) {
    addListDataListener(OnSelectionChangedListener(this, onSelected))
}