package net.saamankhali.portfoliosite.views

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.IEngineConfiguration
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.TemplateResolution

//@Configuration
//class AppTemplateResolver {
//    @Bean
//    fun getAppTemplateResolver(): ClassLoaderTemplateResolver
//    {
//        val templateResolver = ClassLoaderTemplateResolver(javaClass.classLoader)
//
//        templateResolver.prefix = "classpath:/templates2/"
//        templateResolver.suffix = ".html"
//        templateResolver.templateMode = TemplateMode.HTML
//        templateResolver.characterEncoding = "UTF-8"
//        templateResolver.order = 1
//        templateResolver.checkExistence = true
//
//        return templateResolver
//    }
//}