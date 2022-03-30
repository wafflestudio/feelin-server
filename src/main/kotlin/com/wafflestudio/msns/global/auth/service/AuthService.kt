package com.wafflestudio.msns.global.auth.service

import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.exception.AlreadyExistUsernameException
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.dto.AuthRequest
import com.wafflestudio.msns.global.auth.exception.InvalidEmailFormException
import com.wafflestudio.msns.global.auth.exception.InvalidVerificationCodeException
import com.wafflestudio.msns.global.auth.exception.JWTInvalidException
import com.wafflestudio.msns.global.auth.exception.UnauthorizedVerificationTokenException
import com.wafflestudio.msns.global.auth.jwt.JwtTokenProvider
import com.wafflestudio.msns.global.auth.model.VerificationToken
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import com.wafflestudio.msns.global.mail.dto.MailDto
import com.wafflestudio.msns.global.mail.service.MailContentBuilder
import com.wafflestudio.msns.global.mail.service.MailService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.regex.Pattern

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
        if (!isEmailValid(email))
            throw InvalidEmailFormException("Invalid Email Form")

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

    fun signUp(signUpRequest: UserRequest.SignUp): UserResponse.SimpleUserInfo {
        val email = signUpRequest.email
        val lastName = signUpRequest.lastName
        val firstName = signUpRequest.firstName
        val username = signUpRequest.username
        val phoneNumber = signUpRequest.phoneNumber
        val password = passwordEncoder.encode(signUpRequest.password)

        checkVerifiedEmail(email)
        existUsername(username)

        val verification = verificationTokenRepository.findByEmail(email)
        verification!!.password = password
        verificationTokenRepository.save(verification)

        val newUser = User(
            email = email,
            username = username,
            password = password,
            lastName = lastName,
            firstName = firstName,
            phoneNumber = phoneNumber,
        )
        userRepository.save(newUser)

        return UserResponse.SimpleUserInfo(newUser)
    }

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

    fun verifyCode(verifyRequest: AuthRequest.VerifyCode): Boolean {
        val email = verifyRequest.email
        val code = verifyRequest.code

        val verificationToken = verificationTokenRepository.findByEmail(email)!!

        if (code != verificationToken.authenticationCode)
            throw InvalidVerificationCodeException("Invalid code.")
        verificationToken.verification = true
        verificationTokenRepository.save(verificationToken)

        return true
    }

    private fun createRandomCode(): String {
        return (100000..999999).random().toString()
    }

    private fun existUsername(username: String): Boolean {
        if (userRepository.existsByUsername(username))
            throw AlreadyExistUsernameException("This username already exists.")
        return true
    }

    private fun checkVerifiedEmail(email: String): Boolean {
        val verified = verificationTokenRepository.findByEmail(email)!!
        if (!verified.verification)
            throw UnauthorizedVerificationTokenException("Unauthorized email.")
        return true
    }

    private fun generateToken(email: String, code: String): String {
        val jwt = jwtTokenProvider.generateToken(email, join = true)
        val existingToken = verificationTokenRepository.findByEmail(email)

        existingToken?.let {
            it.token = jwt
            it.authenticationCode = code
            verificationTokenRepository.save(it)
        } ?: run {
            verificationTokenRepository.save(
                VerificationToken(
                    email = email,
                    token = passwordEncoder.encode(jwt),
                    authenticationCode = code
                )
            )
        }

        return jwt
    }

    private fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@" +
                "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
                "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." +
                "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
                "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|" +
                "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }
}
