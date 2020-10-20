package net.saamankhali.portfoliosite

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.lang.IllegalStateException
import java.nio.file.Path
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Configuration
@ConfigurationProperties(prefix = "portfolio-site")
class PortfolioSiteProperties {
    fun getFrontendPath(vararg relPathSegments: String): Path = Path.of(frontendLocation, *relPathSegments)

    var siteHosts: Set<String> = setOf("127.0.0.1", "::1", "localhost")

    var createDatabases: Boolean = false
    var downloadDBLocation: String = "downloads.db"
    var frontendLocation: String = "./frontend/"

    var reCAPTCHAPrivateKey: String = ""

    var contactMeEmail: String = ""
}

// Temporary hack because I'm having trouble getting Hibernate validation working properly...
@Component
class PortfolioSitePropertiesValidator @Autowired constructor(siteProperties: PortfolioSiteProperties)
{
    init
    {
        if(siteProperties.reCAPTCHAPrivateKey.isEmpty()) throw IllegalStateException("portfolio-site.reCAPTCHAPrivateKey is required")
        if(siteProperties.contactMeEmail.isEmpty()) throw IllegalStateException("portfolio-site.contactMeEmail is required")
    }
}