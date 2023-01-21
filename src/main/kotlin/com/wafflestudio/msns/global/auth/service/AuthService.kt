package com.wafflestudio.msns.global.auth.service

import com.wafflestudio.msns.domain.playlist.service.WebClientService
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.exception.AlreadyExistUserException
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.dto.AuthRequest
import com.wafflestudio.msns.global.auth.dto.AuthResponse
import com.wafflestudio.msns.global.auth.exception.ForbiddenVerificationTokenException
import com.wafflestudio.msns.global.auth.exception.InvalidSignUpFormException
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
import com.wafflestudio.msns.global.sms.service.SMSService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val mailService: MailService,
    private val smsService: SMSService,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val mailContentBuilder: MailContentBuilder,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val webClientService: WebClientService
) {
    fun verifyEmail(emailRequest: AuthRequest.VerifyEmail): Boolean {
        val email = emailRequest.email

        return userRepository.findByEmail(email)
            ?.let { true }
            ?: return run {
                val code = createRandomCode()
                generateTokenWithEmail(email, code, JWT.JOIN)
                val message = mailContentBuilder.messageBuild(code, "register")
                val mail = MailDto.Email(email, "Register Feelin", message, false)
                mailService.sendMail(mail)
                false
            }
    }

    suspend fun verifyPhone(phoneRequest: AuthRequest.VerifyPhone): Boolean {
        val countryCode = phoneRequest.countryCode
        val phoneNumber = phoneRequest.phoneNumber

        val isNotNew: Boolean = userRepository.existsByPhoneNumberAndCountryCode(phoneNumber, countryCode)
        return if (isNotNew) { true } else {
            val code = createRandomCode()
            generateTokenWithPhone(countryCode, phoneNumber, code, JWT.JOIN)
            smsService.sendSMS(countryCode, phoneNumber, code)
            false
        }
    }

    fun signUp(userId: UUID, signUpRequest: UserRequest.SignUp): ResponseEntity<UserResponse.SimpleUserInfo> {
        val newUser: User =
            if (!signUpRequest.email.isNullOrBlank()) createUserWithEmail(userId, signUpRequest)
            else if (!signUpRequest.phoneNumber.isNullOrBlank()) createUserWithPhoneNumber(userId, signUpRequest)
            else throw InvalidSignUpFormException("either email or phone number is needed for sign-up.")

        webClientService.createUser(userId, signUpRequest.username)
        val accessJWT = jwtTokenProvider.generateToken(userId, JWT.SIGN_IN)
        val refreshJWT = jwtTokenProvider.generateToken(userId, JWT.REFRESH)

        val responseHeaders = HttpHeaders()
        responseHeaders.set("Access-Token", accessJWT)
        responseHeaders.set("Refresh-Token", refreshJWT)

        return userRepository.save(newUser)
            .also { updateTokens(it, accessJWT, refreshJWT) }
            .let {
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .headers(responseHeaders)
                    .body(UserResponse.SimpleUserInfo(it))
            }
    }

    fun createUserWithEmail(userId: UUID, signUpRequest: UserRequest.SignUp): User {
        val email: String = signUpRequest.email!!
        val username = signUpRequest.username
        val encryptedPassword = passwordEncoder.encode(signUpRequest.password)
        if (checkDuplicateUsername(username))
            throw AlreadyExistUserException("User already exists using this username/phoneNumber.")

        verificationTokenRepository.findByEmail(email)
            ?.also { if (!it.verified) throw UnauthorizedVerificationTokenException("email unauthorized") }
            ?: throw VerificationTokenNotFoundException("verification token not created")

        return User(
            userId = userId,
            email = email,
            username = username,
            password = encryptedPassword,
            name = signUpRequest.name,
            birthDate = signUpRequest.birthDate.let { date -> LocalDate.parse(date, DateTimeFormatter.ISO_DATE) },
        )
    }

    fun createUserWithPhoneNumber(userId: UUID, signUpRequest: UserRequest.SignUp): User {
        val countryCode: String = signUpRequest.countryCode!!
        val phoneNumber: String = signUpRequest.phoneNumber!!
        val username = signUpRequest.username
        val encryptedPassword = passwordEncoder.encode(signUpRequest.password)
        if (checkDuplicateUsername(username))
            throw AlreadyExistUserException("User already exists using this username/phoneNumber.")

        verificationTokenRepository.findByCountryCodeAndPhoneNumber(countryCode, phoneNumber)
            ?.also { if (!it.verified) throw UnauthorizedVerificationTokenException("phone unauthorized") }
            ?: throw VerificationTokenNotFoundException("verification token not created")

        return User(
            userId = userId,
            countryCode = countryCode,
            phoneNumber = phoneNumber,
            username = username,
            password = encryptedPassword,
            name = signUpRequest.name,
            birthDate = signUpRequest.birthDate.let { date -> LocalDate.parse(date, DateTimeFormatter.ISO_DATE) },
        )
    }

    fun checkDuplicateUsername(username: String): Boolean = userRepository.existsByUsername(username)

    fun verifyCodeWithEmail(verifyRequest: AuthRequest.VerifyCodeEmail): Boolean =
        verificationTokenRepository.findByEmail(verifyRequest.email)
            ?.also {
                if (!jwtTokenProvider.validateToken(it.accessToken))
                    throw JWTExpiredException("jwt token expired")
            }
            ?.also {
                if (verifyRequest.code != it.authenticationCode)
                    throw InvalidVerificationCodeException("invalid code")
            }
            ?.apply { this.verified = true }
            ?.let { verificationTokenRepository.save(it) }
            ?.let { true }
            ?: false

    fun verifyCodeWithPhone(verifyRequest: AuthRequest.VerifyCodePhone): Boolean =
        verificationTokenRepository.findByCountryCodeAndPhoneNumber(
            verifyRequest.countryCode,
            verifyRequest.phoneNumber
        )
            ?.also {
                if (!jwtTokenProvider.validateToken(it.accessToken))
                    throw JWTExpiredException("jwt token expired")
            }
            ?.also {
                if (verifyRequest.code != it.authenticationCode)
                    throw InvalidVerificationCodeException("invalid code")
            }
            ?.apply { this.verified = true }
            ?.let { verificationTokenRepository.save(it) }
            ?.let { true }
            ?: false

    fun refreshToken(refreshToken: String): AuthResponse.NewToken {
        if (!jwtTokenProvider.validateToken(refreshToken)) throw JWTExpiredException("token is expired.")

        val id = jwtTokenProvider.getIdFromJwt(refreshToken)

        return verificationTokenRepository.findByRefreshToken(refreshToken)
            ?.also {
                if (jwtTokenProvider.validateToken(it.accessToken)) {
                    it.verified = false
                    throw ForbiddenVerificationTokenException("Unauthorized access.")
                }
            }
            ?.apply {
                this.accessToken = jwtTokenProvider.generateToken(id, JWT.SIGN_IN)
                this.refreshToken = jwtTokenProvider.generateToken(id, JWT.REFRESH)
            }
            ?.let { verificationTokenRepository.save(it) }
            ?.let { AuthResponse.NewToken(it.accessToken, it.refreshToken!!) }
            ?: throw VerificationTokenNotFoundException("verification token is not found with the refresh token.")
    }

    fun updateTokens(user: User, accessToken: String, refreshToken: String) {
        val verificationToken = verificationTokenRepository.findByEmail(user.email)
            ?: verificationTokenRepository.findByCountryCodeAndPhoneNumber(user.countryCode, user.phoneNumber)!!
        verificationToken
            .apply {
                this.accessToken = accessToken
                this.refreshToken = refreshToken
                this.verified = true
            }
            .let { verificationTokenRepository.save(it) }
    }

    fun checkAuthorizedByAccessToken(accessToken: String): Boolean =
        verificationTokenRepository.findByAccessToken(accessToken)?.verified == true

    private fun createRandomCode(): String {
        return (100000..999999).random().toString()
    }

    private fun generateTokenWithEmail(email: String, code: String, type: JWT): String {
        val userId = UUID.randomUUID()
        val accessJWT = jwtTokenProvider.generateToken(userId, type)
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
                        authenticationCode = code
                    )
                )
            }

        return accessJWT
    }

    private fun generateTokenWithPhone(
        countryCode: String,
        phoneNumber: String,
        code: String,
        type: JWT
    ): String {
        val userId = UUID.randomUUID()
        val accessJWT = jwtTokenProvider.generateToken(userId, type)
        val existingToken = verificationTokenRepository.findByCountryCodeAndPhoneNumber(countryCode, phoneNumber)

        existingToken
            ?.apply {
                this.accessToken = accessJWT
                this.authenticationCode = code
            }
            ?.let { verificationTokenRepository.save(it) }
            ?: run {
                verificationTokenRepository.save(
                    VerificationToken(
                        countryCode = countryCode,
                        phoneNumber = phoneNumber,
                        accessToken = accessJWT,
                        authenticationCode = code
                    )
                )
            }

        return accessJWT
    }
}
