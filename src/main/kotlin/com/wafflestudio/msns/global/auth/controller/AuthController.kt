package com.wafflestudio.msns.global.auth.controller

import com.wafflestudio.msns.domain.playlist.service.WebClientService
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.global.auth.dto.AuthRequest
import com.wafflestudio.msns.global.auth.dto.AuthResponse
import com.wafflestudio.msns.global.auth.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun signUpEmail(
        @Valid @RequestBody emailRequest: AuthRequest.JoinEmail
    ): AuthResponse.ExistUser {
        return AuthResponse.ExistUser(authService.signUpEmail(emailRequest))
    }

    @PostMapping("/username")
    @ResponseStatus(HttpStatus.OK)
    fun checkDuplicateUsername(
        @Valid @RequestBody usernameRequest: AuthRequest.Username
    ): AuthResponse.ExistUsername =
        AuthResponse.ExistUsername(authService.checkDuplicateUsername(usernameRequest.username))

    @PostMapping("/verify-code")
    @ResponseStatus(HttpStatus.OK)
    fun verifyCode(
        @Valid @RequestBody verifyRequest: AuthRequest.VerifyCode
    ): AuthResponse.VerifyingCode {
        return AuthResponse.VerifyingCode(authService.verifyCode(verifyRequest))
    }

    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody signUpRequest: UserRequest.SignUp
    ): ResponseEntity<UserResponse.SimpleUserInfo> =
        UUID.randomUUID()
            .let { userId -> webClientService.createUser(userId, signUpRequest.username).block()!! }
            .let { authService.signUp(it.id, signUpRequest) }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    fun refreshToken(
        @RequestHeader("Authentication") refreshToken: String,
    ): AuthResponse.NewToken = authService.refreshToken(refreshToken)
}
