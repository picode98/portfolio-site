package net.saamankhali.portfoliosite.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.saamankhali.portfoliosite.PortfolioSiteProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Serializable
data class ReCAPTCHAResponse(val success: Boolean, @SerialName("challenge_ts") val challengeTS: String? = null,
    val hostname: String? = null, @SerialName("error-codes") val errorCodes: List<String>? = null)

@Component
class ReCAPTCHAResponseFactory @Autowired constructor(val siteProperties: PortfolioSiteProperties)
{
    // private class ReCAPTCHARequest(val secret: String, val response: String)

    class ReCAPTCHAErrorResponseException(val responseCode: HttpStatus) :
            Throwable("Google ReCAPTCHA service returned status code ${responseCode.value()}.")

    val responseFormat = Json(JsonConfiguration.Stable)

    fun getResponse(token: String): ReCAPTCHAResponse
    {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val requestMap: MultiValueMap<String, String> = LinkedMultiValueMap(mapOf(
                "secret" to listOf(siteProperties.reCAPTCHAPrivateKey),
                "response" to listOf(token)
        ))
        val requestPayload: HttpEntity<MultiValueMap<String, String>> =
                HttpEntity(requestMap, headers)
        val apiResponse: ResponseEntity<String?> = restTemplate.exchange("https://www.google.com/recaptcha/api/siteverify",
        HttpMethod.POST, requestPayload, String::class.java)
        val responseStr: String? = apiResponse.body
        if(apiResponse.statusCode.is2xxSuccessful)
        {
            return responseFormat.parse(ReCAPTCHAResponse.serializer(), responseStr!!)
        }
        else
        {
            throw ReCAPTCHAErrorResponseException(apiResponse.statusCode)
        }
    }
}