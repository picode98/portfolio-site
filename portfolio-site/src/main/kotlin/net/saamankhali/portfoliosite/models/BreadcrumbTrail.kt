package net.saamankhali.portfoliosite.models

import net.saamankhali.portfoliosite.PortfolioSiteProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.nio.file.Path

data class Breadcrumb(val displayName: String, val linkPath: String)

@Component
class BreadcrumbTrailFactory @Autowired constructor(private val siteProperties: PortfolioSiteProperties)
{
    fun getBreadcrumbTrail(dirPath: Path): List<Breadcrumb>
    {
        val resultTrail = ArrayList<Breadcrumb>()
        val rootPath: Path = siteProperties.getFrontendPath("pages")
        var currentPath: Path = dirPath
        while(true)
        {
            val thisDirConfig = DirConfig.fromFile(currentPath.resolve("dir_info.json").toFile())
            resultTrail.add(Breadcrumb(
                displayName = thisDirConfig.displayName,
                linkPath = rootPath.relativize(currentPath).joinToString("/").let {
                    if(it.isEmpty()) "/" else "/$it/"
                }
            ))

            if(currentPath == rootPath) break

            currentPath = currentPath.parent
        }

        return resultTrail.reversed()
    }
}