package com.wafflestudio.msns.global.auth.repository

import com.wafflestudio.msns.global.auth.model.VerificationToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.transaction.annotation.Transactional

interface VerificationTokenRepository : JpaRepository<VerificationToken, Long?> {
    fun findByEmail(email: String): VerificationToken?

    fun findByAccessToken(accessToken: String): VerificationToken?

    fun findByRefreshToken(refreshToken: String): VerificationToken?

    fun findByCountryCodeAndPhoneNumber(countryCode: String, phoneNumber: String): VerificationToken?

    @Transactional
    @Modifying
    fun deleteVerificationTokenByEmailOrPhoneNumber(email: String, phoneNumber: String)
}
