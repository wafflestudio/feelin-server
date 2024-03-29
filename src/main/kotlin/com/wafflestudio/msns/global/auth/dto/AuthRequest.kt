package com.wafflestudio.msns.global.auth.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class AuthRequest {
    data class VerifyEmail(
        @field:NotBlank @field:Email val email: String,
    )

    data class VerifyPhone(
        @field:NotBlank
        @Pattern(
            regexp = "^\\+?(\\d+)",
            message = "wrong country code format"
        )
        val countryCode: String,

        val phoneNumber: String,
    )

    data class VerifyCodeEmail(
        val email: String,
        val code: String
    )

    data class VerifyCodePhone(
        val countryCode: String,
        val phoneNumber: String,
        val code: String
    )

    data class VerifyUsername(
        val username: String
    )
}
