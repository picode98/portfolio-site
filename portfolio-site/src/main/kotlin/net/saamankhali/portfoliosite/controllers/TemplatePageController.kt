package net.saamankhali.portfoliosite.controllers

import net.saamankhali.portfoliosite.PortfolioSiteProperties
import net.saamankhali.portfoliosite.SiteResourceLoader
import net.saamankhali.portfoliosite.models.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import java.io.File
import java.lang.Exception
import java.lang.RuntimeException
import java.nio.file.Path
import javax.servlet.http.HttpServletRequest

@Controller
class TemplatePageController
    @Autowired constructor(val siteProperties: PortfolioSiteProperties, val metadataFactory: ReleaseListWithMetadataFactory)
{
    // NOTE: Exception classes not deriving from java.lang.RuntimeException
    // (e.g. deriving from Kotlin's Throwable)
    // will throw an IllegalStateException within Spring
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    class PlainPageNotFoundException(path: String)
        : RuntimeException("The path \"$path\" was not found as a plain page.")

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    class DirPageNotFoundException(path: String)
        : RuntimeException("The path \"$path\" was not found as a directory index page.")

    @GetMapping("/test")
    fun fullPageTest(model: Model): String
    {
        model.addAttribute("testServerAttr", "A server-generated string.")
        return "pages/test"
    }

    @GetMapping("/")
    fun getHomeView(request: HttpServletRequest, model: Model): String
    {
        return getDefaultDirView(request, model)
    }

    @GetMapping("/projects/console_calculator/")
    fun getConsoleCalculatorProjectHomepage(model: Model): String
    {
        val releaseInfo = ReleaseList.fromFile(siteProperties.getFrontendPath("/pages/projects/console_calculator/downloads/release_list.json").toFile())
        model.addAttribute("latestVersion", releaseInfo.releases.first().version)
        return "/pages/projects/console_calculator/index"
    }

    @GetMapping("**/changelog")
    fun getDefaultTemplateView(request: HttpServletRequest, model: Model): String = getDefaultPageView(request, model, true)

    @GetMapping("**/{lastSeg:^[^\\.]+\$}")
    fun getDefaultPageView(request: HttpServletRequest, model: Model, usePageAsRootTemplate: Boolean = false): String
    {
        val reqPath: String = request.requestURI

        if(reqPath.endsWith('/'))
        {
            return getDefaultDirView(request, model)
        }

//        var test = SiteResourceLoader().loadResource("/templates/pages/test2.html")
//        println(String(test!!.readAllBytes()))

        if(siteProperties.getFrontendPath("pages$reqPath.html").toFile().isFile)
        {
            if(usePageAsRootTemplate)
            {
                return "pages$reqPath"
            }
            else
            {
                model.addAttribute("sectionDefPath", "pages$reqPath")
                return "templates/plainpageadapter"
            }
        }
        else
        {
            throw PlainPageNotFoundException(reqPath)
        }
    }

    fun getDefaultDirView(request: HttpServletRequest, model: Model): String
    {
        val reqPath: String = request.requestURI
        val dirPath: Path = siteProperties.getFrontendPath("pages$reqPath")
        if(dirPath.toFile().isDirectory)
        {
            val cfgFile: File = siteProperties.getFrontendPath("pages${reqPath}dir_info.json").toFile()
            val dirCfg: DirConfig = DirConfig.fromFile(cfgFile)
            model.addAttribute("subfolderInfoList", getDirInfoList(dirPath, dirCfg))
            return "pages${reqPath}index"
        }
        else
        {
            throw DirPageNotFoundException(reqPath)
        }
    }

    @GetMapping("/**/$DOWNLOAD_URL_PREFIX/")
    fun getDownloadListView(model: Model): String
    {
        // val releaseInfo = ReleaseList.fromFile(siteProperties.getFrontendPath("/pages/projects/console_calculator/downloads/release_list.json").toFile())
        val releaseInfo: ReleaseListWithMetadata = metadataFactory.getList(siteProperties.getFrontendPath("/pages/projects/console_calculator/downloads/"))
        model.addAttribute("releaseInfo", releaseInfo)
        return "/pages/projects/console_calculator/downloads"
    }
}