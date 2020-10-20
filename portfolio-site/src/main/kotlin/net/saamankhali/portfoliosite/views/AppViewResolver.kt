package net.saamankhali.portfoliosite.views

import net.saamankhali.portfoliosite.PortfolioSiteProperties
import net.saamankhali.portfoliosite.models.BreadcrumbTrailFactory
import nz.net.ultraq.thymeleaf.LayoutDialect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.servlet.View
import org.springframework.web.servlet.ViewResolver
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.spring5.view.ThymeleafViewResolver
import java.util.*

@Component
@Order(1)
class AppViewResolver
    @Autowired constructor(private val thymeleafViewResolver: ThymeleafViewResolver,
                           private val engine: SpringTemplateEngine,
                           private val siteNavLoader: NavLoader,
                           private val siteProperties: PortfolioSiteProperties,
                           private val viewProperties: ViewProperties,
                           private val breadcrumbTrailFactory: BreadcrumbTrailFactory) : ViewResolver
{
    init
    {
        engine.addDialect(LayoutDialect())
        thymeleafViewResolver.templateEngine = engine
    }

    override fun resolveViewName(viewName: String, locale: Locale): View? {
        val thymeleafView: View? = thymeleafViewResolver.resolveViewName(viewName, locale)
        return thymeleafView?.let { AppView(it, siteNavLoader.getSiteNav(), siteProperties, viewProperties, breadcrumbTrailFactory) }
    }
}