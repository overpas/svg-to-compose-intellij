package by.overpass.svgtocomposeintellij.domain

import br.com.devsrsouza.svg2compose.VectorType
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.File

class SvgToComposeServiceImplTest {

    private val mockProgressManager: ProgressManager = mock()
    private val mockFactory: SvgToComposeTaskFactory = mock()
    private val mockTask: Task.Backgroundable = mock()

    private val data = SvgToComposeData(
        "",
        "",
        File(""),
        File(""),
        VectorType.SVG,
        "",
    )

    private val service = SvgToComposeService(mockProgressManager, mockFactory)

    @Test
    fun `test svg to compose conversion progress launched`() {
        whenever(mockFactory.createTask(data)).thenReturn(mockTask)

        service.convertSvgToCompose(data)

        verify(mockProgressManager).run(mockTask)
    }
}