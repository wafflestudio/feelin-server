package com.wafflestudio.msns.global.auth.repository

import com.wafflestudio.msns.global.auth.model.VerificationToken
import org.springframework.data.jpa.repository.JpaRepository

interface VerificationTokenRepository : JpaRepository<VerificationToken, Long?> {
    fun findByEmail(email: String?): VerificationToken?
    fun findByAccessToken(accessToken: String): VerificationToken?
    fun findByRefreshToken(refreshToken: String): VerificationToken?
}
