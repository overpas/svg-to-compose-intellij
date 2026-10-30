package by.overpass.svgtocomposeintellij

import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Service(Service.Level.PROJECT)
class ProjectScopeProviderService(private val scope: CoroutineScope) {

    fun launch(block: suspend () -> Unit): Job =
        scope.launch { block() }
}
