package net.saamankhali.portfoliosite

import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset

open class FileFactory<ObjType>
{
    fun fromFile(file: File, factorySerializer: KSerializer<ObjType>, fileFormat: StringFormat = Json(JsonConfiguration.Stable),
            inputCharset: Charset = Charsets.UTF_8): ObjType
    {
        val fileStr = String(FileInputStream(file).readAllBytes(), inputCharset)
        return fileFormat.parse(factorySerializer, fileStr)
    }
}