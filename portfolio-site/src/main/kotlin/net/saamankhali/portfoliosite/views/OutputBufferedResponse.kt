package net.saamankhali.portfoliosite.views

import java.io.CharArrayWriter
import java.io.PrintWriter
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

class OutputBufferedResponse(response: HttpServletResponse) : HttpServletResponseWrapper(response)
{
    private val bufWriter: CharArrayWriter = CharArrayWriter()

    override fun getWriter(): PrintWriter
    {
        return PrintWriter(bufWriter)
    }

    fun getResponseString(): String
    {
        return bufWriter.toString()
    }
}