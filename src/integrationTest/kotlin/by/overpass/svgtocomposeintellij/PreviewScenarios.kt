package by.overpass.svgtocomposeintellij

import com.intellij.driver.client.Remote
import com.intellij.driver.client.service
import com.intellij.driver.model.OnDispatcher
import com.intellij.driver.sdk.openFile
import com.intellij.driver.sdk.singleProject
import com.intellij.driver.sdk.waitFor
import kotlin.time.Duration.Companion.seconds

private const val ICONS_DIR = "src/main/kotlin/com/example/icons"
private const val PREVIEW_EDITOR = "ImageVector Preview"
private val EDITOR_TIMEOUT = 30.seconds

class PreviewScenarios {

    fun hasPreviewEditor(fileName: String): Boolean =
        IdeSession.run("editors-$fileName") {
            val project = singleProject()
            openFile("$ICONS_DIR/$fileName", project)
            val manager = service<OpenEditors>(project)
            waitFor("$fileName is open", EDITOR_TIMEOUT) {
                withContext(OnDispatcher.EDT) { manager.getAllEditors().isNotEmpty() }
            }
            withContext(OnDispatcher.EDT) { manager.getAllEditors().any { it.getName() == PREVIEW_EDITOR } }
        }

    @Remote("com.intellij.openapi.fileEditor.FileEditorManager")
    interface OpenEditors {
        fun getAllEditors(): Array<NamedEditor>
    }

    @Remote("com.intellij.openapi.fileEditor.FileEditor")
    interface NamedEditor {
        fun getName(): String
    }
}
