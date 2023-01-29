package com.wafflestudio.msns.global.auth.jwt

import com.wafflestudio.msns.domain.user.exception.UserNotFoundException
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.model.AuthenticationToken
import com.wafflestudio.msns.global.auth.model.UserPrincipal
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import com.wafflestudio.msns.global.enum.ENV
import com.wafflestudio.msns.global.enum.JWT
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.Key
import java.security.SignatureException
import java.time.Duration
import java.util.Date
import java.util.UUID

@Component
class JwtTokenProvider(
    @Value("\${spring.jwt.secret}") private val jwtSecretKey: String,
    @Value("\${spring.profiles.active}") private val env: String,
    private val userRepository: UserRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
) {
    val tokenPrefix = "Bearer "
    val headerString = "Authorization"
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    fun generateToken(id: UUID, type: JWT): String {
        val claims: MutableMap<String, Any> = hashMapOf("id" to id.toString())
        val now = Date()
        val expiryDate = Date(
            now.time + when (type) {
                JWT.JOIN -> jwtJoinExpirationInMs
                JWT.ACCESS -> jwtExpirationInMs(env)
                JWT.REFRESH -> jwtRefreshExpirationInMs(env)
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
        val verificationTokenPrincipal = authentication.principal as UserPrincipal
        return generateToken(verificationTokenPrincipal.user.id, type)
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

    fun getIdFromJwt(token: String): UUID {
        val tokenWithoutPrefix = removePrefix(token)
        val key: Key = Keys.hmacShaKeyFor(jwtSecretKey.toByteArray())
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(tokenWithoutPrefix)
            .body

        return UUID.fromString(claims.get("id", String::class.java))
    }

    fun getAuthenticationTokenFromJwt(token: String): Authentication {
        val tokenWithoutPrefix = removePrefix(token)
        val key: Key = Keys.hmacShaKeyFor(jwtSecretKey.toByteArray())
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(tokenWithoutPrefix)
            .body

        val id: UUID = UUID.fromString(claims.get("id", String::class.java))
        val user: User = userRepository.findByIdOrNull(id)
            ?: throw UserNotFoundException("user not found with the userId")
        val userPrincipal = UserPrincipal(user)
        val authorities = userPrincipal.authorities

        return AuthenticationToken(userPrincipal, null, authorities)
    }

    companion object {
        private val jwtJoinExpirationInMs: Long = Duration.ofMinutes(10).toMillis()
        private fun jwtExpirationInMs(env: String): Long =
            when (ENV.valueOf(env.uppercase())) {
                ENV.LOCAL -> Duration.ofHours(1).toMillis()
                ENV.DEV -> Duration.ofSeconds(30).toMillis()
                ENV.PROD -> Duration.ofHours(1).toMillis()
            }
        private fun jwtRefreshExpirationInMs(env: String): Long =
            when (ENV.valueOf(env.uppercase())) {
                ENV.LOCAL -> Duration.ofDays(14).toMillis()
                ENV.DEV -> Duration.ofMinutes(2).toMillis()
                ENV.PROD -> Duration.ofDays(14).toMillis()
            }
    }
}
