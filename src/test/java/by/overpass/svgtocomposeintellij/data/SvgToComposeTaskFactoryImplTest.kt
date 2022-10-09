package by.overpass.svgtocomposeintellij.data

import br.com.devsrsouza.svg2compose.VectorType
import by.overpass.svgtocomposeintellij.domain.SvgToComposeData
import by.overpass.svgtocomposeintellij.domain.SvgToComposeDataProcessor
import com.intellij.openapi.project.Project
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.File

class SvgToComposeTaskFactoryImplTest {

    private val mockProject: Project = mock()
    private val mockProcessor: SvgToComposeDataProcessor = mock()

    private val data = SvgToComposeData(
        "",
        "",
        File(""),
        File(""),
        VectorType.SVG,
        "",
    )

    private val factory = SvgToComposeTaskFactoryImpl(mockProject, mockProcessor)

    @Test
    fun `test data processor triggered when task created`() {
        whenever(mockProcessor.invoke(data)).thenReturn(data)

        factory.createTask(data)

        verify(mockProcessor).invoke(data)
    }
}