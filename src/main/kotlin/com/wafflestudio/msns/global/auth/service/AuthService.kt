package com.wafflestudio.msns.global.auth.service

import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.exception.AlreadyExistUserException
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.dto.AuthRequest
import com.wafflestudio.msns.global.auth.exception.InvalidBirthFormException
import com.wafflestudio.msns.global.auth.exception.InvalidEmailFormException
import com.wafflestudio.msns.global.auth.exception.UnauthorizedVerificationTokenException
import com.wafflestudio.msns.global.auth.exception.VerificationTokenNotFoundException
import com.wafflestudio.msns.global.auth.exception.JWTExpiredException
import com.wafflestudio.msns.global.auth.exception.InvalidVerificationCodeException
import com.wafflestudio.msns.global.auth.exception.ForbiddenVerificationTokenException
import com.wafflestudio.msns.global.auth.jwt.JwtTokenProvider
import com.wafflestudio.msns.global.auth.model.VerificationToken
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import com.wafflestudio.msns.global.enum.JWT
import com.wafflestudio.msns.global.mail.dto.MailDto
import com.wafflestudio.msns.global.mail.service.MailContentBuilder
import com.wafflestudio.msns.global.mail.service.MailService
import io.jsonwebtoken.Jwts
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID
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
                generateToken(email, code, JWT.JOIN)
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
            generateToken(email, code, JWT.JOIN)

            val message = mailContentBuilder.messageBuild(code, "signIn")
            val mail = MailDto.Email(email, "SignIn Feelin", message, false)
            mailService.sendMail(mail)
            true
        }
    }

    fun signUp(streamId: UUID, signUpRequest: UserRequest.SignUp): UserResponse.SimpleUserInfo {
        val email = signUpRequest.email
        val lastName = signUpRequest.lastName
        val firstName = signUpRequest.firstName
        val username = signUpRequest.username
        val birth = signUpRequest.birth
            .also { if (!isBirthValid(it)) throw InvalidBirthFormException("Invalid Birth Form") }
            .split("-")
        val phoneNumber = signUpRequest.phoneNumber
        val password = passwordEncoder.encode(signUpRequest.password)

        if (existUser(username, phoneNumber))
            throw AlreadyExistUserException("User already exists using this username/phoneNumber.")

        verificationTokenRepository.findByEmail(email)
            ?.also { if (!it.verification) throw UnauthorizedVerificationTokenException("email is unauthorized.") }
            ?.apply { this.password = password }
            ?: throw VerificationTokenNotFoundException("verification token is not created.")

        val newUser = User(
            email = email,
            username = username,
            password = password,
            lastName = lastName,
            firstName = firstName,
            birth = birth.let { date -> LocalDate.of(date[0].toInt(), date[1].toInt(), date[2].toInt()) },
            phoneNumber = phoneNumber,
            streamId = streamId
        )
        userRepository.save(newUser)

        return UserResponse.SimpleUserInfo(newUser)
    }

    fun checkValidJWT(jwt: String): Boolean = jwtTokenProvider.validateToken(jwt)

    fun verifyCode(verifyRequest: AuthRequest.VerifyCode): Boolean {
        val email = verifyRequest.email
        val code = verifyRequest.code

        return verificationTokenRepository.findByEmail(email)
            ?.also { if (!checkValidJWT(it.accessToken)) throw JWTExpiredException("jwt token is expired.") }
            ?.also { if (code != it.authenticationCode) throw InvalidVerificationCodeException("Invalid code.") }
            ?.apply { this.verification = true }
            ?.let { verificationTokenRepository.save(it) }
            ?.let { true }
            ?: false
    }

    fun refreshToken(refreshToken: String): String {
        if (!jwtTokenProvider.validateToken(refreshToken)) throw JWTExpiredException("token is expired.")

        val email = Jwts.parser()
            .setSigningKey(jwtTokenProvider.jwtSecretKey)
            .parseClaimsJws(jwtTokenProvider.removePrefix(refreshToken))
            .body["email"].toString()

        return verificationTokenRepository.findByRefreshToken(refreshToken)
            ?.also {
                if (jwtTokenProvider.validateToken(it.accessToken)) {
                    it.verification = false
                    throw ForbiddenVerificationTokenException("Unauthorized access.")
                }
            }
            ?.apply { this.accessToken = jwtTokenProvider.generateToken(email, JWT.SIGN_IN) }
            ?.let { verificationTokenRepository.save(it) }
            ?.accessToken
            ?: throw VerificationTokenNotFoundException("verification token is not found with the refresh token.")
    }

    private fun existUser(username: String, phoneNumber: String): Boolean =
        userRepository.existsByUsername(username) || userRepository.existsByPhoneNumber(phoneNumber)

    private fun createRandomCode(): String {
        return (100000..999999).random().toString()
    }

    private fun generateToken(email: String, code: String, type: JWT): String {
        val accessJWT = jwtTokenProvider.generateToken(email, type)
        val refreshJWT = jwtTokenProvider.generateToken(email, JWT.REFRESH)
        val existingToken = verificationTokenRepository.findByEmail(email)

        existingToken
            ?.apply {
                this.accessToken = accessJWT
                this.authenticationCode = code
            }
            ?.let { verificationTokenRepository.save(it) }
            ?: run {
                verificationTokenRepository.save(
                    VerificationToken(
                        email = email,
                        accessToken = accessJWT,
                        refreshToken = refreshJWT,
                        authenticationCode = code
                    )
                )
            }

        return accessJWT
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

    private fun isBirthValid(birth: String): Boolean {
        return Pattern.compile(
            "^\\d{4}-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])\$"
        ).matcher(birth).matches()
    }
}
