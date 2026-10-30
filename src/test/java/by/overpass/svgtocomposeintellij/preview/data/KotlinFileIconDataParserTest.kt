package by.overpass.svgtocomposeintellij.preview.data

import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class KotlinFileIconDataParserTest {

    @Test
    fun `Ab-testing icon is parsed`() =
        runTest {
            val sut = KotlinFileIconDataParser(
                readSource = { File("src/test/resources/kotlin/Ab-testing.kt").readText() },
            )

            val actual = sut.parse()

            assertTrue(actual.isSuccess)
        }

    @Test
    fun `Note icon is parsed`() =
        runTest {
            val sut = KotlinFileIconDataParser(readSource = { File("src/test/resources/kotlin/Note.kt").readText() })

            val actual = sut.parse()

            assertTrue(actual.isSuccess)
        }

    @Test
    fun `InfoCircle (with 2 paths) icon is parsed`() =
        runTest {
            val sut = KotlinFileIconDataParser(
                readSource = { File("src/test/resources/kotlin/InfoCircle.kt").readText() },
            )

            val actual = sut.parse()

            assertTrue(actual.isSuccess)
        }

    @Test
    fun `Clear icon is NOT parsed`() =
        runTest {
            val sut = KotlinFileIconDataParser(readSource = { File("src/test/resources/kotlin/Clear.kt").readText() })

            val actual = sut.parse()

            assertTrue(actual.isFailure)
        }

    @Test
    fun `__MyIconPack file is NOT parsed`() =
        runTest {
            val sut = KotlinFileIconDataParser(readSource = { File("src/test/resources/kotlin/Clear.kt").readText() })

            val actual = sut.parse()

            assertTrue(actual.isFailure)
        }
}
