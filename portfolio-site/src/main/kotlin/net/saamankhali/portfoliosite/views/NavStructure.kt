package net.saamankhali.portfoliosite.views

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.serializersModuleOf
import net.saamankhali.portfoliosite.FileFactory
import net.saamankhali.portfoliosite.PortfolioSiteProperties
import net.saamankhali.portfoliosite.models.ReleaseItem
import net.saamankhali.portfoliosite.models.ReleaseList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import java.util.*

@Serializable
data class NavStructure(var items: List<@ContextualSerialization NavItem>)
{
    companion object : FileFactory<NavStructure>()
    {
        fun fromFile(file: File, siteProperties: PortfolioSiteProperties)
                = fromFile(file, serializer(), Json(JsonConfiguration.Stable,
                                                    serializersModuleOf(NavItem::class, NavItemSerializer(siteProperties))))
//        init {
//            // Auto-generated serializer is not available at the time of FileFactory construction (throws NPE)
//             factorySerializer = serializer()
//        }
    }
}

@Serializable
data class NavItem(var dispName: String, val url: String, val releaseDir: String? = null, val items: List<@ContextualSerialization NavItem> = listOf(),
    @Transient var releaseList: ReleaseList? = null)
{
//    companion object
//    {
//        val NEW_RELEASE_INTERVAL: Duration = Duration.parse("P14D")
//        val UPDATED_RELEASE_INTERVAL: Duration = Duration.parse("P28D")
//    }
//
//    val newUpdatedStatus: NewUpdatedStatus?
//    get() {
//        if(latestReleaseDate != null && singleRelease != null)
//        {
//            val timeSinceRelease = Duration.between(latestReleaseDate!!.toInstant(), Instant.now())
//            return when {
//                (singleRelease!! && timeSinceRelease <= NEW_RELEASE_INTERVAL) -> NewUpdatedStatus.NEW
//                (timeSinceRelease <= UPDATED_RELEASE_INTERVAL) -> NewUpdatedStatus.UPDATED
//                else -> NewUpdatedStatus.NONE
//            }
//        }
//        else
//        {
//            return null
//        }
//    }
}

class NavItemSerializer constructor(private val siteProperties: PortfolioSiteProperties): KSerializer<NavItem>
{
    val baseSerializer: KSerializer<NavItem> = NavItem.serializer()

    override val descriptor: SerialDescriptor
        get() = baseSerializer.descriptor

    override fun deserialize(decoder: Decoder): NavItem {
        val navItem: NavItem = baseSerializer.deserialize(decoder)
        if(navItem.releaseDir != null)
        {
            val releaseInfoPath: Path = siteProperties.getFrontendPath(navItem.releaseDir, "release_list.json")
            navItem.releaseList = ReleaseList.fromFile(releaseInfoPath.toFile())

//            val releasesWithFiles: List<ReleaseItem> = releaseInfo.releases.filter { it.files.isNotEmpty() }
//            var latestReleaseDate: Date? = null
//            releasesWithFiles.forEach { thisRelease ->
//                val thisReleaseLatest = (thisRelease.files.maxBy { it.releaseDate })!!.releaseDate
//                if(latestReleaseDate == null || thisReleaseLatest.after(latestReleaseDate)) {
//                    latestReleaseDate = thisReleaseLatest
//                }
//            }
//
//            navItem.singleRelease = (releasesWithFiles.size == 1)
//            navItem.latestReleaseDate = latestReleaseDate



//            val firstReleaseWithFiles: ReleaseItem? = releaseInfo.releases.firstOrNull { it.files.isNotEmpty() }
//
//            if(firstReleaseWithFiles != null)
//            {
//                val firstReleaseDate: Date = (firstReleaseWithFiles.files.maxBy { it.releaseDate })!!.releaseDate
//
//                if(releaseInfo.releases.size == 1 && )
//            }
        }

        return navItem
    }

    override fun serialize(encoder: Encoder, value: NavItem) = baseSerializer.serialize(encoder, value)

}

@Component
class NavLoader @Autowired constructor(val siteProperties: PortfolioSiteProperties)
{
    fun getSiteNav(): NavStructure = NavStructure.fromFile(siteProperties.getFrontendPath("nav_items.json").toFile(), siteProperties)
}

//@Configuration
//class SiteNav @Autowired constructor(val siteProperties: PortfolioSiteProperties)
//{
//    @Bean
//    fun getSiteNav(): NavStructure = NavStructure.fromFile(siteProperties.getFrontendPath("nav_items.json").toFile(), siteProperties)
//}