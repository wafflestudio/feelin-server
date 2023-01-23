package com.wafflestudio.msns.global.auth.controller

import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.service.UserService
import com.wafflestudio.msns.global.auth.CurrentUser
import com.wafflestudio.msns.global.auth.dto.AuthRequest
import com.wafflestudio.msns.global.auth.dto.AuthResponse
import com.wafflestudio.msns.global.auth.dto.LoginRequest
import com.wafflestudio.msns.global.auth.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val userService: UserService
) {
    @PostMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    fun newEmailCheck(
        @Valid @RequestBody emailRequest: AuthRequest.VerifyEmail
    ): AuthResponse.ExistUser = AuthResponse.ExistUser(authService.verifyEmail(emailRequest))

    @PostMapping("/phone")
    @ResponseStatus(HttpStatus.OK)
    suspend fun newPhoneCheck(
        @Valid @RequestBody phoneRequest: AuthRequest.VerifyPhone
    ): AuthResponse.ExistUser = AuthResponse.ExistUser(authService.verifyPhone(phoneRequest))

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun withdrawal(@CurrentUser user: User) = userService.withdrawUser(user)

    @PostMapping("/username")
    @ResponseStatus(HttpStatus.OK)
    fun checkDuplicateUsername(
        @Valid @RequestBody usernameRequest: AuthRequest.Username
    ): AuthResponse.ExistUsername =
        AuthResponse.ExistUsername(authService.checkDuplicateUsername(usernameRequest.username))

    @PostMapping("/verify-code/email")
    @ResponseStatus(HttpStatus.OK)
    fun verifyCode(
        @Valid @RequestBody verifyRequest: AuthRequest.VerifyCodeEmail
    ): AuthResponse.VerifyingCode = AuthResponse.VerifyingCode(authService.verifyCodeWithEmail(verifyRequest))

    @PostMapping("/verify-code/phone")
    @ResponseStatus(HttpStatus.OK)
    fun verifyCode(
        @Valid @RequestBody verifyRequest: AuthRequest.VerifyCodePhone
    ): AuthResponse.VerifyingCode = AuthResponse.VerifyingCode(authService.verifyCodeWithPhone(verifyRequest))

    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody signUpRequest: UserRequest.SignUp
    ): ResponseEntity<UserResponse.SimpleUserInfo> =
        authService.signUp(UUID.randomUUID(), signUpRequest)

    @PostMapping("/signin")
    fun signup(
        @Valid @RequestBody loginRequest: LoginRequest
    ): ResponseEntity<UserResponse.SimpleUserInfo?> = authService.signIn(loginRequest)

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    fun refreshToken(
        @RequestHeader("Authentication") refreshToken: String,
    ): AuthResponse.NewToken = authService.refreshToken(refreshToken)
}
