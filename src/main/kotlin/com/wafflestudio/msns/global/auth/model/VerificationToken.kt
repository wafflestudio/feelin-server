package com.wafflestudio.msns.global.auth.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "verfiy_token")
class VerificationToken(
    @Column(unique = true)
    @field:NotBlank
    @field:Email
    val email: String,

    var token: String,

    val role: String = "user",

) : BaseTimeEntity()
