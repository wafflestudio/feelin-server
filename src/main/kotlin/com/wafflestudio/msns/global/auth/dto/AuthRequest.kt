package com.wafflestudio.msns.global.auth.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class AuthRequest {
    data class VerifyEmail(
        @field:NotBlank @field:Email val email: String,
    )

    data class VerifyCodeEmail(
        val email: String,
        val code: String,
    )

    data class Username(
        val username: String
    )
}
