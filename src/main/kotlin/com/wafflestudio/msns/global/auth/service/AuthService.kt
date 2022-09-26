package com.wafflestudio.msns.global.auth.service

import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.exception.AlreadyExistUserException
import com.wafflestudio.msns.domain.user.exception.UserNotFoundException
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.dto.AuthRequest
import com.wafflestudio.msns.global.auth.dto.AuthResponse
import com.wafflestudio.msns.global.auth.exception.ForbiddenVerificationTokenException
import com.wafflestudio.msns.global.auth.exception.InvalidBirthFormException
import com.wafflestudio.msns.global.auth.exception.InvalidEmailFormException
import com.wafflestudio.msns.global.auth.exception.InvalidVerificationCodeException
import com.wafflestudio.msns.global.auth.exception.JWTExpiredException
import com.wafflestudio.msns.global.auth.exception.UnauthorizedVerificationTokenException
import com.wafflestudio.msns.global.auth.exception.VerificationTokenNotFoundException
import com.wafflestudio.msns.global.auth.jwt.JwtTokenProvider
import com.wafflestudio.msns.global.auth.model.VerificationToken
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import com.wafflestudio.msns.global.enum.JWT
import com.wafflestudio.msns.global.mail.dto.MailDto
import com.wafflestudio.msns.global.mail.service.MailContentBuilder
import com.wafflestudio.msns.global.mail.service.MailService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    private val jwtTokenProvider: JwtTokenProvider
) {
    fun signUpEmail(emailRequest: AuthRequest.JoinEmail): Boolean {
        val email = emailRequest.email
        if (!isEmailValid(email)) throw InvalidEmailFormException("Invalid Email Form")

        return userRepository.findByEmail(email)
            ?.let { true }
            ?: return run {
                val code = createRandomCode()
                generateToken(email, code, JWT.JOIN)
                val message = mailContentBuilder.messageBuild(code, "register")
                val mail = MailDto.Email(email, "Register Feelin", message, false)
                mailService.sendMail(mail)
                false
            }
    }

    fun signUp(streamId: UUID, signUpRequest: UserRequest.SignUp): ResponseEntity<UserResponse.SimpleUserInfo> {
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

        val verificationToken = verificationTokenRepository.findByEmail(email)
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

        val accessJWT = jwtTokenProvider.generateToken(email, JWT.SIGN_IN)
        val refreshJWT = jwtTokenProvider.generateToken(email, JWT.REFRESH)

        val responseHeaders = HttpHeaders()
        responseHeaders.set("Access-Token", accessJWT)
        responseHeaders.set("Refresh-Token", refreshJWT)

        return userRepository.save(newUser)
            .also { updateTokens(verificationToken, accessJWT, refreshJWT) }
            .let {
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .headers(responseHeaders)
                    .body(UserResponse.SimpleUserInfo(it))
            }
    }

    fun checkDuplicateUsername(username: String): Boolean = userRepository.findByUsername(username) != null

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

    fun refreshToken(refreshToken: String): AuthResponse.NewToken {
        if (!jwtTokenProvider.validateToken(refreshToken)) throw JWTExpiredException("token is expired.")

        val email = jwtTokenProvider.getEmailFromJwt(refreshToken)

        return verificationTokenRepository.findByRefreshToken(refreshToken)
            ?.also {
                if (jwtTokenProvider.validateToken(it.accessToken)) {
                    it.verification = false
                    throw ForbiddenVerificationTokenException("Unauthorized access.")
                }
            }
            ?.apply {
                this.accessToken = jwtTokenProvider.generateToken(email, JWT.SIGN_IN)
                this.refreshToken = jwtTokenProvider.generateToken(email, JWT.REFRESH)
            }
            ?.let { verificationTokenRepository.save(it) }
            ?.let { AuthResponse.NewToken(it.accessToken, it.refreshToken) }
            ?: throw VerificationTokenNotFoundException("verification token is not found with the refresh token.")
    }

    fun updateTokens(verificationToken: VerificationToken, accessToken: String, refreshToken: String) {
        verificationToken
            .apply {
                this.accessToken = accessToken
                this.refreshToken = refreshToken
                this.verification = true
            }
            .let { verificationTokenRepository.save(it) }
    }

    fun checkAuthorizedByAccessToken(accessToken: String): Boolean =
        verificationTokenRepository.findByAccessToken(accessToken)?.verification == true

    private fun existUser(username: String, phoneNumber: String): Boolean =
        userRepository.existsByUsername(username) || userRepository.existsByPhoneNumber(phoneNumber)

    private fun createRandomCode(): String {
        return (100000..999999).random().toString()
    }

    private fun generateToken(email: String, code: String, type: JWT): String {
        val accessJWT = jwtTokenProvider.generateToken(email, type)
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
                        refreshToken = "",
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
