package by.overpass.svgtocomposeintellij

import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
internal class ProjectScopeProviderService(
    val scope: CoroutineScope,
)
