package net.saamankhali.portfoliosite.models.db

import net.saamankhali.portfoliosite.PortfolioSiteProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class DBUtils @Autowired constructor(private val siteProperties: PortfolioSiteProperties)
{
    private val frontendRootPath = Path.of(siteProperties.frontendLocation).toAbsolutePath()

    fun toDBPathString(path: Path): String = frontendRootPath.relativize(path.toAbsolutePath()).joinToString("/")
    fun fromDBPathString(pathString: String) = siteProperties.getFrontendPath(pathString)
}