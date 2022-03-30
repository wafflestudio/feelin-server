package com.wafflestudio.msns.global.auth.controller

import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.global.auth.dto.AuthRequest
import com.wafflestudio.msns.global.auth.dto.AuthResponse
import com.wafflestudio.msns.global.auth.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
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
    ): UserResponse.SimpleUserInfo {
        return authService.signUp(signUpRequest)
    }

    @PostMapping("/user/signout")
    @ResponseStatus(HttpStatus.OK)
    fun signout(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        SecurityContextLogoutHandler().logout(request, response, auth)
    }
}
