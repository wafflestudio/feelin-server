package com.wafflestudio.msns.domain.user.dto

import javax.validation.constraints.NotBlank

class UserRequest {
    data class SignUp(
        @field:NotBlank
        val email: String,

        @field:NotBlank
        val password: String,

        @field:NotBlank
        val lastName: String,

        @field:NotBlank
        val firstName: String,

        @field:NotBlank
        val username: String,

        @field:NotBlank
        val phoneNumber: String,
    )
}
