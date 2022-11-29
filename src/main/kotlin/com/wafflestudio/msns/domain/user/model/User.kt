package com.wafflestudio.msns.domain.user.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.post.model.Post
import java.time.LocalDate
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
@Table(
    name = "user",
    uniqueConstraints = [UniqueConstraint(columnNames = ["username", "phone_number"])],
    indexes = [Index(columnList = "username"), Index(columnList = "phone_number, country_code")]
)
class User(
    @Column(name = "user_id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    val userId: UUID,

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

    @Column(name = "introduction")
    var introduction: String = "",

    @OneToMany(mappedBy = "user")
    val posts: MutableList<Post> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    val likes: MutableList<Like> = mutableListOf()

) : BaseTimeEntity()
