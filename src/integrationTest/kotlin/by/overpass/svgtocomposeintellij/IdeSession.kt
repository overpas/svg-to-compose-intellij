package by.overpass.svgtocomposeintellij

import com.intellij.driver.client.Driver
import com.intellij.driver.sdk.waitForIndicators
import com.intellij.ide.starter.ci.CIServer
import com.intellij.ide.starter.ci.NoCIServer
import com.intellij.ide.starter.di.di
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.plugins.PluginConfigurator
import com.intellij.ide.starter.project.LocalProjectInfo
import com.intellij.ide.starter.runner.Starter
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.copyToRecursively
import kotlin.io.path.createDirectories
import kotlin.time.Duration.Companion.minutes

private const val FIXTURE_PROJECT = "src/integrationTest/resources/test-project"
private const val PROJECT_COPIES = "build/integration-test-projects"
private const val PLUGIN_STACK_FRAME = "at by.overpass.svgtocomposeintellij."
private val INDEXING_TIMEOUT = 5.minutes

object IdeSession {

    init {
        di = DI {
            extend(di)
            bindSingleton<CIServer>(overrides = true) { PluginErrorsReporter }
        }
    }

    fun <T> run(
        testName: String,
        block: Driver.(projectHome: Path) -> T,
    ): T {
        val projectHome = copyFixtureProject()
        val pluginPath = Path(System.getProperty("path.to.build.plugin"))
        val testCase = TestCase(IdeProductProvider.IU, LocalProjectInfo(projectHome))
            .withVersion(System.getProperty("ide.version"))
        val result = AtomicReference<Result<T>>()
        Starter.newContext(testName, testCase)
            .apply { PluginConfigurator(this).installPluginFromPath(pluginPath) }
            .runIdeWithDriver()
            .useDriverAndCloseIde {
                waitForIndicators(INDEXING_TIMEOUT)
                result.set(runCatching { block(projectHome) })
            }
        return result.get().getOrThrow()
    }

    @OptIn(ExperimentalPathApi::class)
    private fun copyFixtureProject(): Path {
        val copies = Path(PROJECT_COPIES).createDirectories()
        val copy = Files.createTempDirectory(copies, "project").absolute()
        return Path(FIXTURE_PROJECT).copyToRecursively(copy, followLinks = false, overwrite = true)
    }

    private object PluginErrorsReporter : CIServer by NoCIServer {

        override fun reportTestFailure(
            testName: String,
            message: String,
            details: String,
            linkToLogs: String?,
        ) {
            if (PLUGIN_STACK_FRAME in details) {
                throw AssertionError("$testName: IDE reported a plugin error: $message\n$details")
            }
        }
    }
}
