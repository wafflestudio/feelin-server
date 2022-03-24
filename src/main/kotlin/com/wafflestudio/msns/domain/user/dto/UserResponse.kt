package com.wafflestudio.msns.domain.user.dto

import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository

class UserResponse {
    data class SimpleUserInfo(
        val id: Long,
        val username: String,
        val email: String,
    ) {
        constructor(user: User) : this(
            id = user.id,
            username = user.username,
            email = user.email,
        )
    }

    data class SignUpResponse(
        val id: Long,
        val username: String,
        val email: String,
        val token: String,
    ) {
        constructor(user: User, verificationTokenRepository: VerificationTokenRepository) : this(
            id = user.id,
            username = user.username,
            email = user.email,
            token = verificationTokenRepository.findByEmail(user.email)!!.token,
        )
    }
}
