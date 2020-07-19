package net.saamankhali.portfoliosite.views

import org.springframework.ui.ModelMap
import org.springframework.web.servlet.View
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AppView(private val origView: View, private val siteNavStructure: NavStructure, private val viewProperties: ViewProperties) : View {
    override fun render(modelMap: MutableMap<String, *>?, request: HttpServletRequest, response: HttpServletResponse)
    {
        val model: ModelMap = modelMap as ModelMap
        model.addAllAttributes(mapOf(
                "_siteNav" to siteNavStructure,
                "_viewProperties" to viewProperties
        ))
        val bufferedResponse = OutputBufferedResponse(response)
        origView.render(modelMap, request, bufferedResponse)

        val outputBuffer: String = bufferedResponse.getResponseString()
        println("Captured buffered output (${outputBuffer.length} chars)")

        val newResponse: String = processResponse(outputBuffer)

        response.setContentLength(newResponse.length)
        response.writer.write(newResponse)
    }

    override fun getContentType(): String? = origView.contentType
}