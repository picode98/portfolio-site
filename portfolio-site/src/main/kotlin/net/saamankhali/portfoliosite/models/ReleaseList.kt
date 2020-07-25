package net.saamankhali.portfoliosite.models

import kotlinx.serialization.*
import net.saamankhali.portfoliosite.FileFactory
import net.saamankhali.portfoliosite.PortfolioSiteProperties
import net.saamankhali.portfoliosite.models.db.DownloadMetadataDBConnector
import net.saamankhali.portfoliosite.views.NavItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap

@Component
class ReleaseListWithMetadataFactory
    @Autowired constructor(val siteProperties: PortfolioSiteProperties, val cacheSvc: DownloadMetadataDBConnector)
{
    fun getList(downloadDir: Path): ReleaseListWithMetadata = ReleaseListWithMetadata(downloadDir, siteProperties.frontendLocation, cacheSvc)
}

class ReleaseListWithMetadata(val downloadDir: Path, val frontendPath: String, val cacheSvc: DownloadMetadataDBConnector)
{
    // val releaseMetadata: Map<>
//    companion object
//    {
//        fun fromFrontendReleaseList(frontendPath: String): ReleaseListWithMetadata
//        {
//            val releaseListFile: File = File
//        }
//    }
    val releaseList: ReleaseList = ReleaseList.fromFile(downloadDir.resolve("release_list.json").toFile())
    val releaseMetadata: MutableMap<String, DownloadMetadata> = HashMap()

    init {
        releaseList.releases.forEach { thisRelease ->
            thisRelease.files.forEach { thisFile ->
                val metadata = DownloadMetadata(cacheSvc, downloadDir.resolve(thisFile.filePath))
                releaseMetadata[thisFile.filePath] = metadata
            }
        }
    }
}

enum class NewUpdatedStatus { NEW, UPDATED, NONE }

@Serializable
data class ReleaseList(/*@Serializable(with = StringMapDeserializer::class)*/ val releases: List<ReleaseItem>)
{/*
    class StringMapDeserializer<ValueType>(val ser1: KSerializer<String>, val ser2: KSerializer<ValueType>) : KSerializer<Map<String, ValueType>>
    {
        override val descriptor: SerialDescriptor
            get() = SerialDescriptor("StringMap", StructureKind.MAP)

        override fun deserialize(decoder: Decoder): Map<String, ValueType> {
            val jsonDecoder = (decoder as? JsonInput) ?: (throw SerializationException("JSON input expected."))
            val rootObject = (decoder.decodeJson() as? JsonObject) ?: (throw SerializationException("Root node must be object."))

            return rootObject.map {
                val thisValue: ValueType = jsonDecoder.json.fromJson(ser2, it.value)
                it.key to thisValue
            }.toMap()
        }

        override fun patch(decoder: Decoder, old: Map<String, ValueType>): Map<String, ValueType> {
            TODO("Not yet implemented")
        }

        override fun serialize(encoder: Encoder, value: Map<String, ValueType>) {
            TODO("Not yet implemented")
        }

    }*/

    /*class DirRelativeSerializer(val rootDir: Path) : KSerializer<ReleaseList>
    {
        private val baseSerializer = ReleaseList.serializer

        override val descriptor: SerialDescriptor
            get() = baseSerializer.descriptor

        override fun deserialize(decoder: Decoder): ReleaseList {
            val list: ReleaseList = decoder.decode(baseSerializer)
            list.releases.forEach { thisRelease ->
                thisRelease.files.forEach { thisFile ->
                    thisFile.filePath = rootDir.resolve(thisFile.filePath)
                }
            }
            return list
        }

        override fun serialize(encoder: Encoder, value: ReleaseList) {
            TODO("Not yet implemented")
        }
    }*/

    data class ReleaseListStats(val singleRelease: Boolean, val latestReleaseDate: Date?)

    val stats: ReleaseListStats by lazy {
        val releasesWithFiles: List<ReleaseItem> = releases.filter { it.files.isNotEmpty() }
        var latestReleaseDate: Date? = null
        releasesWithFiles.forEach { thisRelease ->
            val thisReleaseLatest = (thisRelease.files.maxBy { it.releaseDate })!!.releaseDate
            if(latestReleaseDate == null || thisReleaseLatest.after(latestReleaseDate)) {
                latestReleaseDate = thisReleaseLatest
            }
        }

        return@lazy ReleaseListStats(singleRelease = (releasesWithFiles.size == 1), latestReleaseDate = latestReleaseDate)
    }

    val newUpdatedStatus: NewUpdatedStatus
        get() {
            val listStats: ReleaseListStats = stats
            if(listStats.latestReleaseDate != null)
            {
                val timeSinceRelease = Duration.between(listStats.latestReleaseDate.toInstant(), Instant.now())
                return when {
                    (listStats.singleRelease && timeSinceRelease <= NEW_RELEASE_INTERVAL) -> NewUpdatedStatus.NEW
                    (!listStats.singleRelease && timeSinceRelease <= UPDATED_RELEASE_INTERVAL) -> NewUpdatedStatus.UPDATED
                    else -> NewUpdatedStatus.NONE
                }
            }
            else
            {
                return NewUpdatedStatus.NONE
            }
        }

    companion object : FileFactory<ReleaseList>()
    {
        val NEW_RELEASE_INTERVAL: Duration = Duration.parse("P14D")
        val UPDATED_RELEASE_INTERVAL: Duration = Duration.parse("P28D")

        val factorySerializer = serializer()

        fun fromFile(file: File) = fromFile(file, factorySerializer)
    }
}

@Serializable
data class ReleaseItem(var version: String, var files: List<ReleaseFile>)

@Serializable
data class ReleaseFile(@Serializable(with = DateSerializer::class) var releaseDate: Date, var filePath: String,
                       var displayName: String)