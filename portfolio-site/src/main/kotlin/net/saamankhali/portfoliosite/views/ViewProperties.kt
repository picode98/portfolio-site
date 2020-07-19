package net.saamankhali.portfoliosite.views

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "portfolio-site.views")
class ViewProperties {
    var smartmenusThemeName: String = "sm-simple"
}