package net.saamankhali.portfoliosite

import java.io.InputStream
import java.nio.file.FileSystems

class SiteResourceLoader {
    fun loadResource(relPath: String): InputStream? {
        FileSystems.newFileSystem(javaClass.getResource("").toURI(), mapOf<String, Any>())
        return javaClass.getResourceAsStream(relPath)
    }
}