package net.saamankhali.portfoliosite

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.nio.file.Path

@Configuration
@ConfigurationProperties(prefix = "portfolio-site")
class PortfolioSiteProperties {
    fun getFrontendPath(vararg relPathSegments: String): Path = Path.of(frontendLocation, *relPathSegments)

    var createDatabases: Boolean = false
    var downloadDBLocation: String = "downloads.db"
    var frontendLocation: String = "./frontend/"
}