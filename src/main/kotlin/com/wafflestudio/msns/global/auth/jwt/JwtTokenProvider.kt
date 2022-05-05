package com.wafflestudio.msns.global.auth.jwt

import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.exception.VerificationTokenNotFoundException
import com.wafflestudio.msns.global.auth.model.AuthenticationToken
import com.wafflestudio.msns.global.auth.model.VerificationTokenPrincipal
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProvider(
    private val userRepository: UserRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
) {
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    val tokenPrefix = "Bearer "
    val headerString = "Authentication"

    @Value("\${spring.jwt.jwt-access-secret-key}")
    private val jwtAccessSecretKey: String? = null

    @Value("\${sprint.jwt.jwt-refresh-secret-key}")
    private val jwtRefreshSecretKey: String? = null

    @Value("\${spring.jwt.jwt-access-expiration-in-ms}")
    private val jwtAccessExpirationInMs: Long? = null

    @Value("\${spring.jwt.jwt-refresh-expiration-in-ms}")
    private val jwtRefreshExpirationInMs: Long? = null

    fun generateToken(email: String, isRefresh: Boolean = false): String {
        val claims: MutableMap<String, Any> = hashMapOf("email" to email)
        val now = Date()
        val expiryDate = Date(now.time + (if (!isRefresh) jwtAccessExpirationInMs!! else jwtRefreshExpirationInMs!!))

        return if (!isRefresh) {
            tokenPrefix + Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtAccessSecretKey)
                .compact()
        } else {
            tokenPrefix + Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtRefreshSecretKey)
                .compact()
        }
    }

    fun generateToken(authentication: Authentication): String {
        val verificationTokenPrincipal = authentication.principal as VerificationTokenPrincipal
        return generateToken(verificationTokenPrincipal.verificationToken.email)
    }

    fun validateToken(authToken: String?, isRefresh: Boolean): Boolean {
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
            return if (!isRefresh) {
                Jwts.parser().setSigningKey(jwtAccessSecretKey).parseClaimsJws(authTokenWithoutPrefix)
                true
            } else {
                Jwts.parser().setSigningKey(jwtRefreshSecretKey).parseClaimsJws(authTokenWithoutPrefix)
                true
            }
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

    fun getEmailFromJwt(token: String): String {
        var tokenWithoutPrefix = token
        tokenWithoutPrefix = removePrefix(tokenWithoutPrefix)
        val claims = Jwts.parser()
            .setSigningKey(jwtAccessSecretKey)
            .parseClaimsJws(tokenWithoutPrefix)
            .body

        return claims.get("email", String::class.java)
    }

    fun getAuthenticationTokenFromJwt(token: String): Authentication {
        var tokenWithoutPrefix = token
        tokenWithoutPrefix = removePrefix(tokenWithoutPrefix)
        val claims = Jwts.parser()
            .setSigningKey(jwtAccessSecretKey)
            .parseClaimsJws(tokenWithoutPrefix)
            .body

        val email = claims.get("email", String::class.java)
        val currentUser = userRepository.findByEmail(email)
        val currentAuthToken = verificationTokenRepository.findByEmail(email)
            ?: throw VerificationTokenNotFoundException("$email is not valid email, check token is expired.")
        val userPrincipal = VerificationTokenPrincipal(currentUser, currentAuthToken)
        val authorities = userPrincipal.authorities

        return AuthenticationToken(userPrincipal, null, authorities)
    }
}
