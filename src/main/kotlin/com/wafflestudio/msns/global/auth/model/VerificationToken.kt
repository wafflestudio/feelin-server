package com.wafflestudio.msns.global.auth.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.Email

@Entity
@Table(
    name = "verify_token",
    uniqueConstraints =
    [
        UniqueConstraint(columnNames = ["email"]),
        UniqueConstraint(columnNames = ["phone_number"])
    ]
)
class VerificationToken(
    @Column(name = "email", unique = true)
    @field:Email
    var email: String,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "access_token")
    var accessToken: String,

    @Column(name = "refresh_token")
    var refreshToken: String,

    @Column(name = "authentication_code")
    var authenticationCode: String,

    @Column(name = "is_verified")
    var verified: Boolean = false,

) : BaseTimeEntity()
