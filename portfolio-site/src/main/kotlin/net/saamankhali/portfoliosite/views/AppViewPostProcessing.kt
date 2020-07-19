package net.saamankhali.portfoliosite.views

import net.saamankhali.portfoliosite.models.SITE_NAME
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

fun genPageTitle(origTitle: String): String = "$origTitle | $SITE_NAME"

fun processResponse(responseBody: String): String
{
    val responseDoc : Document = Jsoup.parse(responseBody)
    processResponse(responseDoc)
    return responseDoc.toString()
}

fun processResponse(responseBody: Document)
{
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
}