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
    ): AuthResponse.ExistUser = authService.checkExistUserByEmail(emailRequest)

    @PostMapping("/phone")
    @ResponseStatus(HttpStatus.OK)
    fun newPhoneCheck(
        @Valid @RequestBody phoneRequest: AuthRequest.VerifyPhone
    ): AuthResponse.ExistUser = authService.checkExistUserByPhone(phoneRequest)

    @PostMapping("/username")
    @ResponseStatus(HttpStatus.OK)
    fun checkDuplicateUsername(
        @Valid @RequestBody usernameRequest: AuthRequest.VerifyUsername
    ): AuthResponse.ExistUsername =
        AuthResponse.ExistUsername(authService.checkDuplicateUsername(usernameRequest.username))

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun withdrawal(
        @RequestHeader("Authorization") accessToken: String,
        @CurrentUser user: User
    ) = userService.withdrawUser(user, accessToken)

    @PostMapping("/email/verify-code/send")
    @ResponseStatus(HttpStatus.OK)
    fun sendVerifyingEmail(@Valid @RequestBody emailRequest: AuthRequest.VerifyEmail) =
        authService.sendVerifyingEmail(emailRequest)

    @PostMapping("/phone/verify-code/send")
    @ResponseStatus(HttpStatus.OK)
    suspend fun sendVerifyingPhone(@Valid @RequestBody phoneRequest: AuthRequest.VerifyPhone) =
        authService.sendVerifyingPhone(phoneRequest)

    @PostMapping("/email/verify-code")
    @ResponseStatus(HttpStatus.OK)
    fun verifyCode(
        @Valid @RequestBody verifyRequest: AuthRequest.VerifyCodeEmail
    ): AuthResponse.VerifyingCode = AuthResponse.VerifyingCode(authService.verifyCodeWithEmail(verifyRequest))

    @PostMapping("/phone/verify-code")
    @ResponseStatus(HttpStatus.OK)
    fun verifyCode(
        @Valid @RequestBody verifyRequest: AuthRequest.VerifyCodePhone
    ): AuthResponse.VerifyingCode = AuthResponse.VerifyingCode(authService.verifyCodeWithPhone(verifyRequest))

    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody signUpRequest: UserRequest.SignUp
    ): ResponseEntity<UserResponse.SimpleUserInfo> =
        authService.signUp(signUpRequest)

    @PostMapping("/signin")
    fun signIn(
        @Valid @RequestBody loginRequest: LoginRequest
    ): ResponseEntity<UserResponse.SimpleUserInfo?> = authService.signIn(loginRequest)

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    fun refreshToken(
        @RequestHeader("Authentication") refreshToken: String,
    ): AuthResponse.NewToken = authService.refreshToken(refreshToken)
}
