package net.saamankhali.portfoliosite.models

import java.nio.file.Path

const val SITE_NAME: String = "saamankhali.net"

fun getDownloadsBasePath(): String =
        System.getenv("DOWNLOADS_BASE_PATH") ?: "./downloads"

fun getDownloadPath(path: String): Path = Path.of(getDownloadsBasePath(), path)