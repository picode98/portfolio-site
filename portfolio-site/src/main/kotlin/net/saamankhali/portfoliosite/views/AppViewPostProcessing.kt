package net.saamankhali.portfoliosite.views

import net.saamankhali.portfoliosite.PortfolioSiteProperties
import net.saamankhali.portfoliosite.models.SITE_NAME
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import java.net.URI

fun genPageTitle(origTitle: String): String = "$origTitle | $SITE_NAME"

fun processResponse(siteProperties: PortfolioSiteProperties, viewSettings: ViewProperties, responseBody: String): String
{
    val responseDoc : Document = Jsoup.parse(responseBody)
    processResponse(siteProperties, viewSettings, responseDoc)
    return responseDoc.toString()
}

fun processResponse(siteProperties: PortfolioSiteProperties, viewSettings: ViewProperties, responseBody: Document)
{
    responseBody.getElementsByTag("server-page-component").forEach {
        it.after(
                when(it.attr("name"))
                {
                    "page-title" -> TextNode(responseBody.title())
                    else -> TextNode("")
                }
        ).remove()
    }

    val CHANGELOG_TAGS = mapOf("added" to "added", "fixed" to "fixed", "removed" to "removed")

    responseBody.title(genPageTitle(responseBody.title()))
    val headElement: Element = responseBody.head()
    val viewSettingsElements: Elements = headElement.select("meta[name='SERVER_VIEW_SETTINGS']")

    if(viewSettingsElements.isNotEmpty())
    {
        if(viewSettingsElements.hasAttr("data-replace-changelog-elements"))
        {
            CHANGELOG_TAGS.forEach { (tagName, tagText) ->
                responseBody.getElementsByTag(tagName)
                        .tagName("div").addClass("change-tag").addClass("change-tag-$tagName")
                        .forEach { it.text(tagText) }
            }

            responseBody.body().getElementsByClass("changelog-item").forEach { thisElement ->
                val releaseVersion: String = thisElement.attr("data-version")
                val linkNode: Element = responseBody.createElement("a")
                        .attr("id", releaseVersion)
                        .attr("href", "#$releaseVersion")
                        .text("Version $releaseVersion")
                thisElement.prependChild(linkNode)
            }
        }

        viewSettingsElements.remove()
    }

    val shortDescElement: Element? = responseBody.body().selectFirst("template#short-desc")

    if(shortDescElement != null)
    {
        if(!shortDescElement.attr("data-create-meta-tag").equals("false", ignoreCase = true))
        {
            headElement.prependChild(
                    responseBody.createElement("meta")
                            .attr("name", "description")
                            .attr("content", shortDescElement.text())
            )
        }

        shortDescElement.remove()
    }

    if(viewSettings.showExternalLinkArrow)
    {
        responseBody.body().getElementsByTag("a").filter {thisElement ->
            if(thisElement.hasAttr("href"))
            {
                val host: String? = URI(thisElement.attr("href")).host
                (host != null && !siteProperties.siteHosts.contains(host))
            }
            else true
        }.forEach { it.addClass("external-link") };
    }
}