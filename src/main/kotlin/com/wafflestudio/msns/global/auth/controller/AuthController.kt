package com.wafflestudio.msns.global.auth.controller

import com.wafflestudio.msns.domain.playlist.service.WebClientService
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.global.auth.dto.AuthRequest
import com.wafflestudio.msns.global.auth.dto.AuthResponse
import com.wafflestudio.msns.global.auth.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val webClientService: WebClientService
) {
    @PostMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    fun signUpEmail(
        @Valid @RequestBody emailRequest: AuthRequest.JoinEmail
    ): AuthResponse.ExistUser {
        return AuthResponse.ExistUser(authService.signUpEmail(emailRequest))
    }

    @PostMapping("/user/verify-code")
    @ResponseStatus(HttpStatus.OK)
    fun verifyCode(
        @Valid @RequestBody verifyRequest: AuthRequest.VerifyCode
    ): AuthResponse.VerifyingCode {
        return AuthResponse.VerifyingCode(authService.verifyCode(verifyRequest))
    }

    @PostMapping("/user/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(
        @Valid @RequestBody signUpRequest: UserRequest.SignUp
    ): UserResponse.SimpleUserInfo =
        UUID.randomUUID()
            .let { userId -> webClientService.createUser(userId, signUpRequest.username).block()!! }
            .let { authService.signUp(it.id, signUpRequest) }

    @PostMapping("/user/signin/refresh")
    @ResponseStatus(HttpStatus.OK)
    fun refreshToken(
        @RequestHeader("Refresh-Token") refreshToken: String,
    ): AuthResponse.NewAccessToken = AuthResponse.NewAccessToken(authService.refreshToken(refreshToken))
}
