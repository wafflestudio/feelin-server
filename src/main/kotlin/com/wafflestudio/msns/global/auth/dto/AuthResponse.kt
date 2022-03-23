package com.wafflestudio.msns.global.auth.dto

import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.global.auth.model.VerificationTokenPrincipal

class AuthResponse {
    data class ExistUser(
        val existUser: Boolean,
    )

    data class VerifyingCode(
        val verification: Boolean,
    )

    data class VerificationTokenPrincipalResponse(
        val user: UserResponse.SimpleUserInfo?,
        val token: String,
    ) {
        constructor(verificationTokenPrincipal: VerificationTokenPrincipal, jwtToken: String) : this(
            user = verificationTokenPrincipal.user?.let { UserResponse.SimpleUserInfo(it) },
            token = jwtToken,
        )
    }
}
