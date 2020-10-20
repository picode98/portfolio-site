package net.saamankhali.portfoliosite.views

import org.hibernate.validator.constraints.NotEmpty
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError

data class ContactFormData(var returnEmail: String? = null, var messageSubject: String? = null,
                           var messageText: String? = null, var gReCAPTCHAResponse: String? = null)
{
    fun validate(): MutableList<String>
    {
        val errList: ArrayList<String> = ArrayList()
        if(messageSubject.isNullOrEmpty()) errList.add("No message subject was supplied.")
        if(messageText.isNullOrEmpty()) errList.add("No message text was supplied.")
        if(gReCAPTCHAResponse.isNullOrEmpty()) errList.add("reCAPTCHA response was not supplied. (Be sure to check the \"I'm not a robot\" checkbox.)")

        return errList
    }
}