package com.wafflestudio.msns.global.auth.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.dto.AuthResponse
import com.wafflestudio.msns.global.auth.dto.LoginRequest
import com.wafflestudio.msns.global.auth.jwt.JwtTokenProvider
import com.wafflestudio.msns.global.auth.model.VerificationTokenPrincipal
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
) : UsernamePasswordAuthenticationFilter(authenticationManager) {
    init {
        setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher("/api/v1/auth/user/signin", "POST"))
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication,
    ) {
        val jwt = jwtTokenProvider.generateToken(authResult)
        response.addHeader("Authentication", jwt)
        response.status = HttpServletResponse.SC_OK
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"

        val body = response.writer

        val principal = authResult.principal as VerificationTokenPrincipal
        val userJsonString =
            objectMapper.writeValueAsString(AuthResponse.VerificationTokenPrincipalResponse(principal, jwt))

        body.print(userJsonString)
        body.flush()
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
        var loginEmail: String = ""

        val parsedRequest: LoginRequest = parseRequest(request)
        val account = parsedRequest.account
        val password = parsedRequest.password

        if (userRepository.findByUsername(account!!) == null && userRepository.findByPhoneNumber(account) == null) {
            loginEmail = account
        } else if (userRepository.findByUsername(account) == null && userRepository.findByEmail(account) == null) {
            loginEmail = userRepository.findByPhoneNumber(account)!!.email
        } else if (userRepository.findByPhoneNumber(account) == null && userRepository.findByEmail(account) == null) {
            loginEmail = userRepository.findByUsername(account)!!.email
        }

        val authRequest: Authentication =
            UsernamePasswordAuthenticationToken(loginEmail, password)
        return authenticationManager.authenticate(authRequest)
    }

    private fun parseRequest(request: HttpServletRequest): LoginRequest {
        val reader: BufferedReader = request.reader
        val objectMapper = ObjectMapper()
        return objectMapper.readValue(reader, LoginRequest::class.java)
    }
}
