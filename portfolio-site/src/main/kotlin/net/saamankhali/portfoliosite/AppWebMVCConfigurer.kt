package net.saamankhali.portfoliosite

import net.saamankhali.portfoliosite.views.DataSizeFormatter
import org.springframework.format.FormatterRegistry
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Component
class AppWebMVCConfigurer : WebMvcConfigurer {
    override fun addFormatters(registry: FormatterRegistry) {
        registry.addFormatter(DataSizeFormatter())
        super.addFormatters(registry)
    }
}