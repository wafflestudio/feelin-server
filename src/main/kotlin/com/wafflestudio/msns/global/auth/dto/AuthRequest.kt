package com.wafflestudio.msns.global.auth.dto

class AuthRequest {
    data class JoinEmail(
        val email: String,
    )

    data class VerifyCode(
        val email: String,
        val code: String,
    )
}
