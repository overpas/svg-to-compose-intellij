package by.overpass.svgtocomposeintellij.preview.data

import com.intellij.openapi.vfs.VirtualFile
import java.io.InputStream
import java.net.JarURLConnection
import java.net.URL
import kotlin.io.path.inputStream

fun VirtualFile.asInputStream(): InputStream {
    return if (fileSystem.protocol == "jar") {
        val url = URL("jar:file:$path")
        val jarUrlConnection = url.openConnection() as JarURLConnection
        jarUrlConnection.inputStream
    } else {
        toNioPath().inputStream()
    }
}
