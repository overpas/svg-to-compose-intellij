package by.overpass.svgtocomposeintellij.generator.data

import by.overpass.svgtocomposeintellij.generator.domain.SourceRootResolver
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File

class ProjectSourceRootResolver(private val project: Project) : SourceRootResolver {

    override fun resolve(dir: File): File? =
        ApplicationManager.getApplication().runReadAction(
            Computable {
                LocalFileSystem.getInstance().findFileByIoFile(dir)
                    ?.let(ProjectFileIndex.getInstance(project)::getSourceRootForFile)
                    ?.let { sourceRoot -> File(sourceRoot.path) }
            },
        )
}
