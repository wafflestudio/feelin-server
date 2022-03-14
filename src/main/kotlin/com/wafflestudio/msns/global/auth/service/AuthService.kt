package com.wafflestudio.msns.global.auth.service

import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.dto.AuthRequest
import com.wafflestudio.msns.global.auth.exception.JWTInvalidException
import com.wafflestudio.msns.global.auth.jwt.JwtTokenProvider
import com.wafflestudio.msns.global.auth.model.VerificationToken
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import com.wafflestudio.msns.global.mail.dto.MailDto
import com.wafflestudio.msns.global.mail.service.MailContentBuilder
import com.wafflestudio.msns.global.mail.service.MailService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val mailService: MailService,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val mailContentBuilder: MailContentBuilder,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    fun signUpEmail(emailRequest: AuthRequest.JoinEmail): Boolean {
        val email = emailRequest.email
        userRepository.findByEmail(email)
            ?: return run {
                val code = createRandomCode()
                generateToken(email, code)

                val message = mailContentBuilder.messageBuild(code, "register")
                val mail = MailDto.Email(email, "Register Feelin", message, false)
                mailService.sendMail(mail)
                false
            }
        return signInEmail(emailRequest)
    }

    fun signInEmail(emailRequest: AuthRequest.JoinEmail): Boolean {
        val email = emailRequest.email
        userRepository.findByEmail(email)
            ?: return signUpEmail(emailRequest)
        return run {
            val code = createRandomCode()
            generateToken(email, code)

            val message = mailContentBuilder.messageBuild(code, "signIn")
            val mail = MailDto.Email(email, "SignIn Feelin", message, false)
            mailService.sendMail(mail)
            true
        }
    }

    // fun signUp(signUpRequest: UserRequest.SignUp): AuthResponse.VerificationTokenPrincipalResponse {
    //     val email = signUpRequest.email
    //     val lastName = signUpRequest.lastName
    //     val firstName = signUpRequest.firstName
    //     val username = signUpRequest.username
    //     val phoneNumber = signUpRequest.phoneNumber
    //     val password = passwordEncoder.encode(signUpRequest.password)
    //
    //
    // }

    fun verifyJWT(email: String, jwt: String): VerificationToken {
        if (!jwtTokenProvider.validateToken(jwt))
            throw JWTInvalidException("Token is invalid.")
        jwtTokenProvider.getEmailFromJwt(jwt)
            .let {
                if (it != email)
                    throw JWTInvalidException("JWT does not correspond to the email.")
            }
        val verificationToken = verificationTokenRepository.findByEmail(email)!!
            .also {
                if (!passwordEncoder.matches(jwt, it.token))
                    throw JWTInvalidException("JWT does not correspond to any join request.")
            }
        return verificationToken
    }

    // private fun checkVerificationCode(email: String, inputCode: String) : Boolean {
    //     val verificationCode = verificationTokenRepository.findByEmail(email)!!.authenticationCode
    //     if (!passwordEncoder.matches(inputCode, verificationCode))
    //         throw InvalidVerificationCodeException("This code is invalid.")
    //     return true
    // }

    fun verifyCode(verifyRequest: AuthRequest.VerifyCode): Boolean {
        val email = verifyRequest.email
        val code = verifyRequest.code

        val verificationToken = verificationTokenRepository.findByEmail(email)

        return verificationToken!!.authenticationCode == code
    }

    private fun createRandomCode(): String {
        return (100000..999999).random().toString()
    }

    private fun generateToken(email: String, code: String): String {
        val jwt = jwtTokenProvider.generateToken(email, join = true)
        val existingToken = verificationTokenRepository.findByEmail(email)

        existingToken?.let {
            it.token = jwt
            it.authenticationCode = code
            verificationTokenRepository.save(it)
        } ?: run {
            verificationTokenRepository.save(VerificationToken(email, passwordEncoder.encode(jwt), code))
        }

        return jwt
    }
}
