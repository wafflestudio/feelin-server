package com.wafflestudio.msns.global.auth.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table
import javax.validation.constraints.Email

@Entity
@Table(
    name = "verify_token",
    indexes = [
        Index(
            name = "unique_idx_pn_cc_email",
            columnList = "phone_number, country_code, email",
            unique = true
        )
    ]
)
class VerificationToken(
    @Column(name = "email", unique = true)
    @field:Email
    var email: String? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "country_code")
    var countryCode: String? = null,

    @Column(name = "access_token")
    var accessToken: String,

    @Column(name = "refresh_token")
    var refreshToken: String? = null,

    @Column(name = "authentication_code")
    var authenticationCode: String,

    @Column(name = "is_verified")
    var verified: Boolean = false,

) : BaseTimeEntity()
