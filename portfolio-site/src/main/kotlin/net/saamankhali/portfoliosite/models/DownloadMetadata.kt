package net.saamankhali.portfoliosite.models

import net.saamankhali.portfoliosite.models.db.DownloadMetadataDBConnector
import java.nio.file.Path
import java.util.*

data class CachedDownloadMetadata(var path: Path, var datetime_modified: Date, var sha1: String, var sha2_256: String)

data class DownloadMetadata(var path: Path, var datetime_modified: Date, var sha1: String, var sha2_256: String, var fileSize: Long)
{
    constructor(cachedMetadata: CachedDownloadMetadata, fileSize: Long)
            : this(cachedMetadata.path, cachedMetadata.datetime_modified, cachedMetadata.sha1, cachedMetadata.sha2_256, fileSize)

    constructor(cacheSvc: DownloadMetadataDBConnector, path: Path)
            : this(cacheSvc.getCachedMetadata(path), path.toFile().length())
}