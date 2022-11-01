package com.wafflestudio.msns.domain.user.dto

import java.util.UUID
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class UserRequest {
    data class SignUp(
        val email: String,
        val phoneNumber: String,
        val countryCode: String,
        @field:NotBlank val password: String,
        @field:NotBlank val name: String,
        @field:NotBlank val username: String,
        @field:NotBlank val birthDate: String
    )

    data class PostAPIDto(
        @field:NotBlank val id: UUID,
        @field:NotBlank val username: String
    )

    data class PutProfile(
        @field:NotBlank val username: String,
        @field:NotNull val profileImageUrl: String,
        @field:NotNull val introduction: String
    )
}
