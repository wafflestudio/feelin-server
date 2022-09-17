package com.wafflestudio.msns.domain.user.dto

import java.util.UUID
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

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

        @field:NotBlank
        val birth: String
    )

    data class PostAPIDto(
        @field:NotBlank
        val id: UUID,

        @field:NotBlank
        val username: String
    )

    data class PutProfile(
        @field:NotBlank
        val username: String,

        @field:NotNull
        val image: String,

        @field:NotNull
        val introduction: String
    )
}
