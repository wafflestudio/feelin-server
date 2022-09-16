package com.wafflestudio.msns.global.auth.dto

class AuthResponse {
    data class ExistUser(
        val existUser: Boolean,
    )

    data class VerifyingCode(
        val verification: Boolean,
    )

    data class NewAccessToken(
        val accessToken: String
    )
}
