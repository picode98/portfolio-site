package net.saamankhali.portfoliosite.views

import net.saamankhali.portfoliosite.PortfolioSiteProperties
import net.saamankhali.portfoliosite.models.Breadcrumb
import net.saamankhali.portfoliosite.models.BreadcrumbTrailFactory
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.View
import java.nio.file.Path
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AppView(private val origView: View, private val siteNavStructure: NavStructure,
              private val siteProperties: PortfolioSiteProperties, private val viewProperties: ViewProperties,
              private val breadcrumbTrailFactory: BreadcrumbTrailFactory) : View {
    override fun render(modelMap: MutableMap<String, *>?, request: HttpServletRequest, response: HttpServletResponse)
    {
        val requestPathStr: String = request.requestURI.removePrefix("/")

        val breadcrumbTrail: List<Breadcrumb> = if(requestPathStr.isNotEmpty())
        {
            val requestPath = Path.of(requestPathStr)
            val breadcrumbDirPath = siteProperties.getFrontendPath("pages").resolve(requestPath).parent
            breadcrumbTrailFactory.getBreadcrumbTrail(breadcrumbDirPath)
        }
        else listOf()

        val model: ModelMap = modelMap as ModelMap
        model.addAllAttributes(mapOf(
                "_siteNav" to siteNavStructure,
                "_viewProperties" to viewProperties,
                "_breadcrumbTrail" to breadcrumbTrail
        ))
        val bufferedResponse = OutputBufferedResponse(response)
        origView.render(modelMap, request, bufferedResponse)

        val outputBuffer: String = bufferedResponse.getResponseString()
        println("Captured buffered output (${outputBuffer.length} chars)")

        val newResponse: String = processResponse(siteProperties, viewProperties, outputBuffer)

        response.setContentLength(newResponse.length)
        response.writer.write(newResponse)
    }

    override fun getContentType(): String? = origView.contentType
}