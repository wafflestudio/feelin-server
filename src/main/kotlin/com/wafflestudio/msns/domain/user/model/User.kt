package com.wafflestudio.msns.domain.user.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.post.model.Post
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(
    name = "user",
    indexes = [
        Index(
            name = "unique_idx_username",
            columnList = "username",
            unique = true
        ),
        Index(
            name = "unique_idx_pn_cc_email",
            columnList = "phone_number, country_code, email",
            unique = true
        )
    ]
)
class User(
    @field:NotBlank
    @Column(name = "username", unique = true)
    var username: String,

    @field:Email
    @Column(name = "email", unique = true)
    val email: String = "",

    @field:NotBlank
    @Column(name = "password")
    var password: String,

    @Column(name = "role")
    var role: String = "user",

    @Column(name = "country_code")
    var countryCode: String = "",

    @Column(name = "phone_number")
    var phoneNumber: String = "",

    @Column(name = "name")
    var name: String,

    @Column(name = "birth_date")
    var birthDate: LocalDate,

    @Column(name = "profile_image_url")
    var profileImageUrl: String? = null,

    @Size(max = 500)
    @Column(name = "introduction", length = 500)
    var introduction: String = "",

    @OneToMany(mappedBy = "user")
    val posts: MutableList<Post> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    val likes: MutableList<Like> = mutableListOf()

) : BaseTimeEntity()
