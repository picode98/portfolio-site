package net.saamankhali.portfoliosite.controllers

import net.saamankhali.portfoliosite.PortfolioSiteProperties
import net.saamankhali.portfoliosite.models.db.DownloadStatsDBConnector
import net.saamankhali.portfoliosite.models.getDownloadPath
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.OutputStream
import java.nio.file.Path
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val DOWNLOAD_URL_PREFIX: String = "downloads"

@ResponseStatus(HttpStatus.NOT_FOUND)
class DownloadFileNotFoundException(filePath: String) : FileNotFoundException("Could not find the download file \"$filePath\".")

@RestController
class DownloadController
    @Autowired constructor(val siteProperties: PortfolioSiteProperties, val statsDBConnector: DownloadStatsDBConnector)
{
    @GetMapping("/**/$DOWNLOAD_URL_PREFIX/**")
    fun downloadFile(request: HttpServletRequest, response: HttpServletResponse)
    {
        val requestPath: String = request.servletPath
        // val relativeURL: String = requestPath.removePrefix(DOWNLOAD_URL_PREFIX)
        val downloadPath: Path = siteProperties.getFrontendPath("pages", requestPath) // getDownloadPath(relativeURL)
        val downloadFile: File = downloadPath.toFile()

        if(!downloadFile.isFile) throw DownloadFileNotFoundException(downloadPath.toString())

        statsDBConnector.incrementCounts(downloadPath)
        println("Will download: $downloadPath")

        response.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
        response.setHeader("Content-Disposition", "attachment; filename=\"${downloadFile.name}\"")
        response.setContentLengthLong(downloadFile.length())

        val BUF_SIZE: Int = 1048576
        val buffer = ByteArray(BUF_SIZE)
        val fileIn = FileInputStream(downloadFile)
        val responseOut: OutputStream = response.outputStream

        var bytesRead: Int = fileIn.read(buffer)
        while(bytesRead != -1)
        {
            responseOut.write(buffer, 0, bytesRead)
            bytesRead = fileIn.read(buffer)
        }

        fileIn.close()
    }
}