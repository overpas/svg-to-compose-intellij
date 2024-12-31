package by.overpass.svgtocomposeintellij

import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
class ProjectScopeProviderService(
    val scope: CoroutineScope,
)
