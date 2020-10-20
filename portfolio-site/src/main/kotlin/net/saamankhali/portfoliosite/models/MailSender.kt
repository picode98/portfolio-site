package net.saamankhali.portfoliosite.models

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

@Component
class AppMailSender @Autowired constructor(private val mailSender: MailSender)
{
    fun sendTextMessage(from: String, to: String, subject: String, text: String, replyTo: String? = null)
    {
        val testMessage = SimpleMailMessage()
        testMessage.setFrom(from)
        testMessage.setTo(to)
        testMessage.setSubject(subject)
        testMessage.setText(text)
        if(replyTo != null) testMessage.setReplyTo(replyTo)

        mailSender.send(testMessage)
    }

//    fun sendTextMessage()
//    {
//        val message = SimpleMailMessage()
//        message.
//        mailSender.send()
//    }
}