package by.overpass.svgtocomposeintellij.preview.data

import com.intellij.openapi.vfs.VirtualFile
import java.net.JarURLConnection
import java.net.URI
import kotlin.io.path.readText

fun VirtualFile.readContent(): String =
    if (fileSystem.protocol == "jar") {
        val url = URI("jar:file:$path").toURL()
        val jarUrlConnection = JarURLConnection::class.java.cast(url.openConnection())
        jarUrlConnection.inputStream.bufferedReader().use { reader -> reader.readText() }
    } else {
        toNioPath().readText()
    }

fun VirtualFile.containsImageVector(): Boolean =
    inputStream
        .bufferedReader()
        .useLines { lines ->
            lines.any { line ->
                imageVectorDeclarationPattern.containsMatchIn(line)
            }
        }
