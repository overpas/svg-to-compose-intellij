package by.overpass.svgtocomposeintellij.generator.data

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.PsiTestUtil
import java.io.File

class ProjectSourceRootResolverTest : HeavyPlatformTestCase() {

    private val root by lazy { tempDir.createVirtualDir() }

    private val sut by lazy { ProjectSourceRootResolver(project) }

    fun `test nested dir resolves to its source root`() {
        val sourceRoot = runWriteAction { VfsUtil.createDirectoryIfMissing(root, "generated") }
        val nestedDir = runWriteAction { VfsUtil.createDirectoryIfMissing(root, "generated/com/example/kotlin/icons") }
        PsiTestUtil.addSourceRoot(module, sourceRoot)

        val actual = sut.resolve(File(nestedDir.path))

        assertEquals(File(sourceRoot.path), actual)
    }

    fun `test dir outside source roots resolves to null`() {
        val dir = runWriteAction { VfsUtil.createDirectoryIfMissing(root, "vectors") }

        val actual = sut.resolve(File(dir.path))

        assertNull(actual)
    }
}
