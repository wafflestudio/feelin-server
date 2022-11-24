package com.wafflestudio.msns.global.auth.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.Email

@Entity
@Table(
    name = "verify_token",
    uniqueConstraints = [UniqueConstraint(columnNames = ["email"]), UniqueConstraint(columnNames = ["phone_number", "country_code"])]
)
class VerificationToken(
    @Column(name = "user_id")
    var userId: UUID,

    @Column(name = "email", unique = true)
    @field:Email
    val email: String,

    @Column(name = "phone_number")
    val phoneNumber: String? = null,

    @Column(name = "country_code")
    val countryCode: String? = null,

    @Column(name = "access_token")
    var accessToken: String,

    @Column(name = "refresh_token")
    var refreshToken: String,

    @Column(name = "password")
    var password: String? = null,

    @Column(name = "authentication_code")
    var authenticationCode: String,

    @Column(name = "is_verified")
    var verified: Boolean = false,

    @Column(name = "role")
    val role: String = "user",

    ) : BaseTimeEntity()
