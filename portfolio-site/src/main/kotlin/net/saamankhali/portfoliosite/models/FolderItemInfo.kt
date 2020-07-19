package net.saamankhali.portfoliosite.models

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

data class FolderItemInfo(val itemName: String, val displayName: String, val descMarkup: String?)
{
    companion object
    {
        fun fromFile(HTMLFile: File, inputCharset: Charset = Charsets.UTF_8): FolderItemInfo
        {
            val fileContents = String(FileInputStream(HTMLFile).readAllBytes(), inputCharset)
            val document: Document = Jsoup.parse(fileContents)
            val shortDesc: String? = document.body().selectFirst("template#short-desc")?.html()
            return FolderItemInfo(itemName = HTMLFile.nameWithoutExtension, displayName = document.title(), descMarkup = shortDesc)
        }
    }
}

fun isPageFile(path: Path) = path.toFile().extension == "html"

fun getDirInfoList(path: Path, config: DirConfig): List<FolderItemInfo>
{
    var folderNameList: List<Path>? = null
    var fileNameList: List<Path>? = null

    if(!config.subfolderVisibility.isAllowList || !config.subfolderVisibility.isAllowList)
    {
        val itemList: List<Path> = Files.list(path).use { it.collect(Collectors.toList()) }

        if(!config.subfolderVisibility.isAllowList)
        {
            folderNameList = itemList.filter { Files.isDirectory(it) && !config.subfolderVisibility.itemNames.contains(it.fileName.toString()) }
        }

        if(!config.fileVisibility.isAllowList)
        {
            fileNameList = itemList.filter { isPageFile(it) && Files.isRegularFile(it) && !config.fileVisibility.itemNames.contains(it.fileName.toString()) }
        }
    }

    if(config.subfolderVisibility.isAllowList)
    {
        folderNameList = config.subfolderVisibility.itemNames.map { path.resolve(it) }.filter { Files.isDirectory(it) }
    }

    if(config.fileVisibility.isAllowList)
    {
        fileNameList = config.fileVisibility.itemNames.map { path.resolve(it) }.filter { isPageFile(it) && Files.isRegularFile(it) }
    }

    return folderNameList!!.map {
        val cfg = DirConfig.fromFile(it.resolve("dir_info.json").toFile())
        FolderItemInfo(it.fileName.toString(), cfg.displayName, cfg.descMarkup)
    } + fileNameList!!.map {
        FolderItemInfo.fromFile(it.toFile())
    }
}

//            .map { DirConfig.fromFile(it.resolve("dir_info.json").toFile()) }
//            .map { DirInfo(it.displayName, it.descMarkup) }
//
//} !!