package com.wafflestudio.msns.global.mail.service

import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class MailContentBuilder(
    private val templateEngine: TemplateEngine
) {
    fun messageBuild(code: String, type: String): String {
        val context = Context()
        context.setVariable("code", code)
        return when (type) {
            "register" -> templateEngine.process("registerMailTemplate", context)
            "signIn" -> templateEngine.process("signInMailTemplate", context)
            else -> ""
        }
    }
}
