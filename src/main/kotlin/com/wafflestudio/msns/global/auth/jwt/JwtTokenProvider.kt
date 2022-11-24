package com.wafflestudio.msns.global.auth.jwt

import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.exception.VerificationTokenNotFoundException
import com.wafflestudio.msns.global.auth.model.AuthenticationToken
import com.wafflestudio.msns.global.auth.model.VerificationTokenPrincipal
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import com.wafflestudio.msns.global.enum.JWT
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.Key
import java.security.SignatureException
import java.time.Duration
import java.util.Date
import java.util.UUID

@Component
class JwtTokenProvider(
    private val userRepository: UserRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
) {
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    val tokenPrefix = "Bearer "
    val headerString = "Authentication"

    @Value("\${spring.jwt.secret}")
    val jwtSecretKey: String = ""

    private val jwtJoinExpirationInMs: Long = Duration.ofMinutes(10).toMillis()

    private val jwtExpirationInMs: Long = Duration.ofHours(1).toMillis()

    private val jwtRefreshExpirationInMs: Long = Duration.ofDays(14).toMillis()

    fun generateToken(email: String, type: JWT): String {
        val claims: MutableMap<String, Any> = hashMapOf("email" to email)
        val now = Date()
        val expiryDate = Date(
            now.time + when (type) {
                JWT.JOIN -> jwtExpirationInMs
                JWT.SIGN_UP -> jwtJoinExpirationInMs
                JWT.SIGN_IN -> jwtExpirationInMs
                JWT.REFRESH -> jwtRefreshExpirationInMs
            }
        )
        val key: Key = Keys.hmacShaKeyFor(jwtSecretKey.toByteArray())
        return tokenPrefix + Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateToken(authentication: Authentication, type: JWT): String {
        val verificationTokenPrincipal = authentication.principal as VerificationTokenPrincipal
        return generateToken(verificationTokenPrincipal.verificationToken.email, type)
    }

    fun validateToken(authToken: String?): Boolean {
        if (authToken.isNullOrEmpty()) {
            logger.error("Token is not provided")
            return false
        }
        if (!authToken.startsWith(tokenPrefix)) {
            logger.error("Token not match type Bearer")
            return false
        }
        val authTokenWithoutPrefix = removePrefix(authToken)
        try {
            val key: Key = Keys.hmacShaKeyFor(jwtSecretKey.toByteArray())
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authTokenWithoutPrefix)
            return true
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
        }
        return false
    }

    fun removePrefix(tokenWithPrefix: String): String {
        return tokenWithPrefix.replace(tokenPrefix, "").trim { it <= ' ' }
    }

    fun getIdFromJwt(token: String): String {
        val tokenWithoutPrefix = removePrefix(token)
        val key: Key = Keys.hmacShaKeyFor(jwtSecretKey.toByteArray())
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(tokenWithoutPrefix)
            .body

        return claims.get("id", String::class.java)
    }

    fun getAuthenticationTokenFromJwt(token: String): Authentication {
        val tokenWithoutPrefix = removePrefix(token)
        val key: Key = Keys.hmacShaKeyFor(jwtSecretKey.toByteArray())
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(tokenWithoutPrefix)
            .body

        val id = claims.get("id", UUID::class.java)
        val user = userRepository.findByUserId(id)
            ?: throw VerificationTokenNotFoundException("user not found")
        val authToken = verificationTokenRepository.findByEmail(user.email)
            ?: throw VerificationTokenNotFoundException("verification token not found")
        val userPrincipal = VerificationTokenPrincipal(user, authToken)
        val authorities = userPrincipal.authorities

        return AuthenticationToken(userPrincipal, null, authorities)
    }
}
