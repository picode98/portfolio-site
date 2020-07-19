package net.saamankhali.portfoliosite.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.saamankhali.portfoliosite.FileFactory
import java.io.File

@Serializable
data class DirConfig(
        @SerialName("fileVisibility") val fileVisibility: ItemVisibility = ItemVisibility(isAllowList = true),
        @SerialName("folderVisibility") val subfolderVisibility: ItemVisibility = ItemVisibility(),
        val displayName: String, val descMarkup: String)
{
    companion object : FileFactory<DirConfig>()
    {
        fun fromFile(file: File) = fromFile(file, serializer())
    }
}

@Serializable
data class ItemVisibility(val isAllowList: Boolean = false, val itemNames: Set<String> = HashSet<String>())