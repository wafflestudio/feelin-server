package com.wafflestudio.msns.global.auth.dto

class AuthResponse {
    data class ExistUser(
        val userExists: Boolean,
    )

    data class VerifyingCode(
        val verification: Boolean,
    )

    data class NewToken(
        val accessToken: String,
        val refreshToken: String
    )

    data class ExistUsername(
        val existUsername: Boolean,
    )
}
