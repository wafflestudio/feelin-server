package com.wafflestudio.msns.global.mail.service

import com.wafflestudio.msns.global.mail.dto.MailDto
import org.apache.commons.io.IOUtils
import org.springframework.core.io.ByteArrayResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.net.URL
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Service
class MailService(
    private val emailSender: JavaMailSender
) {
    fun sendMail(email: MailDto.Email) {
        val message =
            if (email.withAttachment) createMessageWithAttachment(email)
            else createSimpleMessage(email)
        emailSender.send(message)
    }

    private fun createSimpleMessage(email: MailDto.Email): MimeMessage {
        val message: MimeMessage = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message)
        setupMessage(helper, email)
        helper.setText(email.text, true)
        message.setFrom(InternetAddress("authenticationfeelin@gmail.com"))

        return message
    }

    private fun createMessageWithAttachment(email: MailDto.Email): MimeMessage {
        val message: MimeMessage = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        val kotlinIconStream =
            URL("https://upload.wikimedia.org/wikipedia/commons/7/74/Kotlin_Icon.png")
                .openStream()
        val springIconStream =
            URL("https://upload.wikimedia.org/wikipedia/commons/4/44/Spring_Framework_Logo_2018.svg")
                .openStream()

        setupMessage(helper, email)
        message.setFrom(InternetAddress("authenticationfeelin@gmail.com"))
        helper
            .addAttachment("KotlinIcon.png", ByteArrayResource(IOUtils.toByteArray(kotlinIconStream)))
        helper
            .addAttachment("SpringIcon.svg", ByteArrayResource(IOUtils.toByteArray(springIconStream)))
        return message
    }

    private fun setupMessage(helper: MimeMessageHelper, email: MailDto.Email) {
        helper.setTo(email.to)
        helper.setSubject(email.subject)
        helper.setText((email.text))
    }
}
