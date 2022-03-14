package com.wafflestudio.msns.global.auth.controller

import com.wafflestudio.msns.global.auth.dto.AuthRequest
import com.wafflestudio.msns.global.auth.dto.AuthResponse
import com.wafflestudio.msns.global.auth.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUpEmail(
        @Valid @RequestBody emailRequest: AuthRequest.JoinEmail
    ): AuthResponse.ExistUser {
        return AuthResponse.ExistUser(authService.signUpEmail(emailRequest))
    }
}
