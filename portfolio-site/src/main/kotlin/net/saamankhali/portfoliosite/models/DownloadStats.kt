package net.saamankhali.portfoliosite.models

import java.nio.file.Path

data class DownloadStats(val path: Path, val downloads_all_time: Int, val downloads_this_week: Int)