package com.wafflestudio.msns.global.auth.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.dto.LoginRequest
import com.wafflestudio.msns.global.auth.jwt.JwtTokenProvider
import com.wafflestudio.msns.global.auth.model.UserPrincipal
import com.wafflestudio.msns.global.auth.service.AuthService
import com.wafflestudio.msns.global.enum.JWT
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.io.BufferedReader
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SignInAuthenticationFilter(
    authenticationManager: AuthenticationManager?,
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper,
    private val userRepository: UserRepository,
    private val authService: AuthService
) : UsernamePasswordAuthenticationFilter(authenticationManager) {
    init {
        setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher("/api/v1/auth/signin", "POST"))
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication,
    ) {
        val accessJWT = jwtTokenProvider.generateToken(authResult, JWT.ACCESS)
        val refreshJWT = jwtTokenProvider.generateToken(authResult, JWT.REFRESH)
        response.addHeader("Access-Token", accessJWT)
        response.addHeader("Refresh-Token", refreshJWT)
        response.status = HttpServletResponse.SC_OK
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"

        val body = response.writer

        val principal = authResult.principal as UserPrincipal
        val userJsonString =
            objectMapper.writeValueAsString(principal.user.let { UserResponse.SimpleUserInfo(it) })

        body.print(userJsonString)
        body.flush()

        authService.updateTokens(principal.user, accessJWT, refreshJWT)
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException,
    ) {
        super.unsuccessfulAuthentication(request, response, failed)
        response.status = HttpServletResponse.SC_UNAUTHORIZED
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val parsedRequest: LoginRequest = parseRequest(request)
        val account = parsedRequest.account
        val password = parsedRequest.password

        val authRequest: Authentication =
            UsernamePasswordAuthenticationToken(account, password)
        return authenticationManager.authenticate(authRequest)
    }

    private fun parseRequest(request: HttpServletRequest): LoginRequest {
        val reader: BufferedReader = request.reader
        val objectMapper = ObjectMapper()
        return objectMapper.readValue(reader, LoginRequest::class.java)
    }
}
