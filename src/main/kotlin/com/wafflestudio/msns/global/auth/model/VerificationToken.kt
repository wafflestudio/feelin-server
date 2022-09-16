package com.wafflestudio.msns.global.auth.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "verify_token")
class VerificationToken(
    @Column(unique = true)
    @field:NotBlank
    @field:Email
    val email: String,

    var accessToken: String,

    var refreshToken: String,

    var password: String? = null,

    var authenticationCode: String,

    var verification: Boolean = false,

    val role: String = "user",

) : BaseTimeEntity()
