package com.wafflestudio.msns.global.auth.service

import com.wafflestudio.msns.domain.playlist.service.PlaylistClientService
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.exception.AlreadyExistUserException
import com.wafflestudio.msns.domain.user.exception.SignInFailedException
import com.wafflestudio.msns.domain.user.exception.UserNotFoundException
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.dto.AuthRequest
import com.wafflestudio.msns.global.auth.dto.AuthResponse
import com.wafflestudio.msns.global.auth.dto.LoginRequest
import com.wafflestudio.msns.global.auth.exception.ForbiddenVerificationTokenException
import com.wafflestudio.msns.global.auth.exception.InvalidBirthFormException
import com.wafflestudio.msns.global.auth.exception.InvalidSignUpFormException
import com.wafflestudio.msns.global.auth.exception.InvalidVerificationCodeException
import com.wafflestudio.msns.global.auth.exception.JWTExpiredException
import com.wafflestudio.msns.global.auth.exception.UnauthorizedVerificationTokenException
import com.wafflestudio.msns.global.auth.exception.VerificationTokenNotFoundException
import com.wafflestudio.msns.global.auth.jwt.JwtTokenProvider
import com.wafflestudio.msns.global.auth.model.VerificationToken
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import com.wafflestudio.msns.global.enum.JWT
import com.wafflestudio.msns.global.enum.Verify
import com.wafflestudio.msns.global.mail.dto.MailDto
import com.wafflestudio.msns.global.mail.service.MailContentBuilder
import com.wafflestudio.msns.global.mail.service.MailService
import com.wafflestudio.msns.global.sms.service.SMSService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val mailService: MailService,
    private val smsService: SMSService,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val mailContentBuilder: MailContentBuilder,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val playlistClientService: PlaylistClientService,
    @Value("\${spring.verified.phone}") private val verifiedNumbers: List<String>,
) {
    fun checkExistUserByEmail(emailRequest: AuthRequest.VerifyEmail): AuthResponse.ExistUser =
        AuthResponse.ExistUser(userRepository.existsByEmail(emailRequest.email))

    fun checkExistUserByPhone(phoneRequest: AuthRequest.VerifyPhone): AuthResponse.ExistUser =
        AuthResponse.ExistUser(
            userRepository.existsByPhoneNumberAndCountryCode(phoneRequest.phoneNumber, phoneRequest.countryCode)
        )

    fun sendVerifyingEmail(emailRequest: AuthRequest.VerifyEmail) {
        val newEmail: String = emailRequest.email

        val code = createRandomCode()
        generateTokenWithEmail(newEmail, code, JWT.JOIN)
        val message = mailContentBuilder.messageBuild(code, "register")
        val mail = MailDto.Email(newEmail, "Join Feelin", message, false)
        mailService.sendMail(mail)
    }

    suspend fun sendVerifyingPhone(phoneRequest: AuthRequest.VerifyPhone) {
        val newCountryCode = phoneRequest.countryCode
        val newPhoneNumber = phoneRequest.phoneNumber

        if (verifiedNumbers.contains(newCountryCode + newPhoneNumber))
            generateTokenWithPhone(newCountryCode, newPhoneNumber, code = "000000", JWT.JOIN)
        else {
            val code = createRandomCode()
            generateTokenWithPhone(newCountryCode, newPhoneNumber, code = code, JWT.JOIN)
            smsService.sendSMS(newCountryCode, newPhoneNumber, code)
        }
    }

    fun signUp(signUpRequest: UserRequest.SignUp): ResponseEntity<UserResponse.SimpleUserInfo> {
        val newUser: User =
            if (!signUpRequest.email.isNullOrBlank())
                createUserWithEmail(signUpRequest)
            else if (!signUpRequest.phoneNumber.isNullOrBlank() && !signUpRequest.countryCode.isNullOrBlank())
                createUserWithPhoneNumber(signUpRequest)
            else throw InvalidSignUpFormException("either email or phone is needed for sign-up.")

        playlistClientService.createUser(newUser.id, signUpRequest.username)
        val accessJWT = jwtTokenProvider.generateToken(newUser.id, JWT.ACCESS)
        val refreshJWT = jwtTokenProvider.generateToken(newUser.id, JWT.REFRESH)

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

    fun signIn(loginRequest: LoginRequest): ResponseEntity<UserResponse.SimpleUserInfo?> {
        val account = loginRequest.account
        val password = loginRequest.password
        return when (loginRequest.type) {
            Verify.EMAIL -> {
                val user = userRepository.findByEmail(account)
                    ?: throw UserNotFoundException("user not found with email")
                signInWithUser(user, password)
            }
            Verify.PHONE -> {
                val countryCode = account.substring(0 until 3)
                val phoneNumber = account.substring(3)
                val user = userRepository.findByCountryCodeAndPhoneNumber(countryCode, phoneNumber)
                    ?: throw UserNotFoundException("user not found with phone")
                signInWithUser(user, password)
            }
            Verify.NONE -> throw SignInFailedException("need valid verification type")
        }
    }

    fun signInWithUser(user: User, password: String): ResponseEntity<UserResponse.SimpleUserInfo?> {
        val isMatched: Boolean = passwordEncoder.matches(password, user.password)
        if (!isMatched) throw SignInFailedException("wrong password")

        val accessJWT = jwtTokenProvider.generateToken(user.id, JWT.ACCESS)
        val refreshJWT = jwtTokenProvider.generateToken(user.id, JWT.REFRESH)

        val responseHeaders = HttpHeaders()
        responseHeaders.set("Access-Token", accessJWT)
        responseHeaders.set("Refresh-Token", refreshJWT)

        return userRepository.save(user)
            .also { updateTokens(it, accessJWT, refreshJWT) }
            .let {
                ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(responseHeaders)
                    .body(UserResponse.SimpleUserInfo(it))
            }
    }

    fun createUserWithEmail(signUpRequest: UserRequest.SignUp): User {
        val email: String = signUpRequest.email!!
        val username = signUpRequest.username
        val encryptedPassword = passwordEncoder.encode(signUpRequest.password)
        if (checkDuplicateUsername(username))
            throw AlreadyExistUserException("User already exists using this username/phoneNumber.")

        verificationTokenRepository.findByEmail(email)
            ?.also { if (!it.verified) throw UnauthorizedVerificationTokenException("email unauthorized") }
            ?: throw VerificationTokenNotFoundException("verification token not created")

        return User(
            email = email,
            username = username,
            password = encryptedPassword,
            name = signUpRequest.name,
            birthDate = signUpRequest.birthDate.let { date -> LocalDate.parse(date, DateTimeFormatter.ISO_DATE) },
        )
    }

    fun createUserWithPhoneNumber(signUpRequest: UserRequest.SignUp): User {
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
            countryCode = countryCode,
            phoneNumber = phoneNumber,
            username = username,
            password = encryptedPassword,
            name = signUpRequest.name,
            birthDate = signUpRequest.birthDate.let { date ->
                try {
                    LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
                } catch (e: DateTimeParseException) {
                    throw InvalidBirthFormException("invalid birth date form")
                }
            },
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
                this.accessToken = jwtTokenProvider.generateToken(id, JWT.ACCESS)
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

    private fun createRandomCode(): String = ThreadLocalRandom.current().nextInt(100000, 1000000).toString()

    private fun generateTokenWithEmail(email: String, code: String, type: JWT): String {
        val accessJWT = jwtTokenProvider.generateToken(UUID.randomUUID(), type)
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
        val accessJWT = jwtTokenProvider.generateToken(UUID.randomUUID(), type)
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
