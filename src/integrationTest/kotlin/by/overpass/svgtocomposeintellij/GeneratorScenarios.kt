package by.overpass.svgtocomposeintellij

import com.intellij.driver.client.Driver
import com.intellij.driver.client.service
import com.intellij.driver.sdk.invokeAction
import com.intellij.driver.sdk.ui.components.common.IdeaFrameUI
import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.common.toolwindows.projectView
import com.intellij.driver.sdk.ui.components.elements.DialogUiComponent
import com.intellij.driver.sdk.ui.components.elements.dialog
import com.intellij.driver.sdk.ui.components.elements.popupMenu
import com.intellij.driver.sdk.ui.components.elements.waitForNoOpenedDialogs
import com.intellij.driver.sdk.ui.remote.SwingHierarchyService
import com.intellij.driver.sdk.waitFor
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.isRegularFile
import kotlin.io.path.relativeTo
import kotlin.io.path.walk
import kotlin.io.path.writeText
import kotlin.time.Duration.Companion.seconds

private const val GENERATOR_TITLE = "Generate Compose Icons From SVG Or Android Vector Drawables"
private const val ACTION_TEXT = "Jetpack Compose ImageVector"
private const val OUTPUT_DIR = "src/main/kotlin/com/example/icons"
private const val VECTORS_DIR = "vectors"
private const val GENERATE = "Generate"
private const val CANCEL = "Cancel"
private const val DIAGNOSTICS_DIR = "build/integration-test-diagnostics"
private const val TAB_PROBES = 8
private val DIALOG_TIMEOUT = 30.seconds

class GeneratorScenarios {

    fun isDialogShown(): Boolean =
        IdeSession.run("dialog-shown") {
            withDialog { dialogHandle ->
                dialogHandle.present().also { dialogHandle.pressButton(CANCEL) }
            }
        }

    fun isGenerateEnabledWithEmptyInput(): Boolean =
        IdeSession.run("generate-disabled") {
            withDialog { dialogHandle ->
                generateButton(dialogHandle).isEnabled().also { dialogHandle.pressButton(CANCEL) }
            }
        }

    fun filesAfterCancel(): List<String> =
        IdeSession.run("cancel") { projectHome ->
            withDialog { dialogHandle -> dialogHandle.pressButton(CANCEL) }
            ideFrame { waitForNoOpenedDialogs() }
            generatedFiles(projectHome)
        }

    fun filesAfterGenerate(): List<String> =
        IdeSession.run("generate") { projectHome ->
            withDialog { dialogHandle ->
                keyboard { tab() }
                probeTabOrder(dialogHandle)
            }
            generatedFiles(projectHome)
        }

    private fun Driver.probeTabOrder(dialogHandle: DialogUiComponent) {
        val diagnostics = Path(DIAGNOSTICS_DIR).createDirectories()
        diagnostics.resolve("dialog.xml").writeText(
            service(SwingHierarchyService::class).getSwingHierarchyAsDOM(null, false),
        )
        repeat(TAB_PROBES) { step ->
            takeScreenshot(File(diagnostics.toFile(), "tab-$step").absolutePath)
            dialogHandle.keyboard { tab() }
        }
    }

    private fun <T> Driver.withDialog(block: IdeaFrameUI.(DialogUiComponent) -> T): T {
        val frame = ideFrame()
        frame.requireInputFocus()
        invokeAction("ActivateProjectToolWindow")
        frame.projectView().projectViewTree.apply {
            waitForNodesLoaded()
            expandAll()
            rightClickRow { "icons" in it }
        }
        frame.popupMenu().select("New")
        frame.popupMenu().selectContains(ACTION_TEXT)
        val dialogHandle = frame.dialog(title = GENERATOR_TITLE)
        waitFor("generator dialog is open", DIALOG_TIMEOUT) { dialogHandle.present() }
        return frame.block(dialogHandle)
    }

    private fun generateButton(dialogHandle: DialogUiComponent) =
        dialogHandle.x("//div[@class='JButton' and @visible_text='$GENERATE']")

    private fun generatedFiles(projectHome: Path): List<String> {
        val output = projectHome.resolve(OUTPUT_DIR)
        val fixtureFiles = setOf("Note.kt", "Plain.kt")
        return if (output.exists()) {
            output.walk()
                .filter { it.isRegularFile() }
                .map { it.relativeTo(output).invariantSeparatorsPathString }
                .filterNot { it in fixtureFiles }
                .sorted()
                .toList()
        } else {
            emptyList()
        }
    }
}
