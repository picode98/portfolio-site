package net.saamankhali.portfoliosite.models.db

import net.saamankhali.portfoliosite.PortfolioSiteProperties
import net.saamankhali.portfoliosite.models.CachedDownloadMetadata
import net.saamankhali.portfoliosite.models.getDownloadPath
import net.saamankhali.portfoliosite.models.getDownloadsBasePath
import net.saamankhali.portfoliosite.toHexString
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import org.springframework.util.Base64Utils
import org.sqlite.date.DateFormatUtils
import org.sqlite.date.FastDateFormat
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Path
import java.security.MessageDigest
import java.text.Format
import java.util.*
import kotlin.math.abs

@Service
class DownloadMetadataDBConnector @Autowired constructor(val setupHelper: DownloadDBSetupHelper, val siteProperties: PortfolioSiteProperties)
{
    private val dbTemplate: JdbcTemplate = setupHelper.dbTemplate
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val dateFormat: FastDateFormat = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT
    private val frontendRootPath = Path.of(siteProperties.frontendLocation).toAbsolutePath()

    private fun toDBPathString(path: Path): String = frontendRootPath.relativize(path.toAbsolutePath()).toString()
    private fun fromDBPathString(pathString: String) = siteProperties.getFrontendPath(pathString)

    private fun computeCachedMetadata(path: Path): CachedDownloadMetadata
    {
        val file: File = path.toFile()

        if(file.isFile)
        {
            val BUF_SIZE: Int = 1048576
            val buffer = ByteArray(BUF_SIZE)
            val fileIn = FileInputStream(file)
            val sha1Generator: MessageDigest = MessageDigest.getInstance("SHA-1")
            val sha2_256Generator: MessageDigest = MessageDigest.getInstance("SHA-256")

            var bytesRead: Int = fileIn.read(buffer)
            while(bytesRead != -1)
            {
                sha1Generator.update(buffer, 0, bytesRead)
                sha2_256Generator.update(buffer, 0, bytesRead)
                bytesRead = fileIn.read(buffer)
            }

            fileIn.close()
            val sha1Str: String = sha1Generator.digest().toHexString()
            val sha2_256Str: String = sha2_256Generator.digest().toHexString()
            val modDateTime = Date(file.lastModified())

            return CachedDownloadMetadata(path, modDateTime, sha1Str, sha2_256Str)
        }
        else
        {
            throw FileNotFoundException("File \"$path\" not found.")
        }
    }

    private fun addCachedMetadata(metadata: CachedDownloadMetadata)
    {
//        dbTemplate.update(
//            """
//            UPDATE DownloadMetadata
//            SET sha_1 = ?, sha_2_256 = ?
//            WHERE path = ?
//            """.trimIndent(), metadata.sha1, metadata.sha2_256, path)

        dbTemplate.update(
            """
            INSERT INTO DownloadMetadata(path, datetime_modified, sha_1, sha_2_256)
            VALUES(?, ?, ?, ?)
            """.trimIndent(), toDBPathString(metadata.path), dateFormat.format(metadata.datetime_modified), metadata.sha1, metadata.sha2_256)
    }

    private fun updateCachedMetadata(metadata: CachedDownloadMetadata)
    {
        dbTemplate.update(
                """
              UPDATE DownloadMetadata
              SET datetime_modified = ?, sha_1 = ?, sha_2_256 = ?
              WHERE path = ?
            """.trimIndent(), dateFormat.format(metadata.datetime_modified), metadata.sha1, metadata.sha2_256, toDBPathString(metadata.path))
    }

    public fun getCachedMetadata(path: Path): CachedDownloadMetadata
    {
        val downloadFile: File = path.toFile()
        if(!(downloadFile.exists())) throw FileNotFoundException("File \"$path\" not found.")

        val resultList: List<CachedDownloadMetadata> = dbTemplate.query("""
            SELECT path, datetime_modified, sha_1, sha_2_256
            FROM DownloadMetadata
            WHERE path = ?
            """.trimIndent(),
            RowMapper {resultSet, _ ->
                CachedDownloadMetadata(
                        fromDBPathString(resultSet.getString("path")),
                        dateFormat.parse(resultSet.getString("datetime_modified")),
                        resultSet.getString("sha_1"),
                        resultSet.getString("sha_2_256")
                )
            }, toDBPathString(path))

        if(resultList.isNotEmpty())
        {
            val cachedResult: CachedDownloadMetadata = resultList[0]

            if(abs(downloadFile.lastModified() - cachedResult.datetime_modified.time) >= 2000)
            {
                logger.debug("Updating existing metadata for \"{}\"", path)

                val newMetadata: CachedDownloadMetadata = computeCachedMetadata(path)
                updateCachedMetadata(newMetadata)
                return newMetadata
            }
            else
            {
                logger.debug("Found existing, up-to-date metadata for \"{}\"", path)

                return cachedResult
            }
        }
        else
        {
            logger.debug("Adding new metadata for \"{}\"", path)

            val newMetadata: CachedDownloadMetadata = computeCachedMetadata(path)
            addCachedMetadata(newMetadata)
            return newMetadata
        }
    }
}