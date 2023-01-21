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

        @field:NotBlank
        @Pattern(
            regexp = "^[0-9]{3}[-]+[0-9]{4}[-]+[0-9]{4}\$",
            message = "wrong phone number format"
        )
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

    data class Username(
        val username: String
    )
}
