package com.wafflestudio.msns.domain.user.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.post.model.Post
import java.time.LocalDate
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "user")
class User(
    @field:NotBlank
    @Column(unique = true)
    var username: String,

    @field:NotBlank
    @field:Email
    @Column(unique = true)
    val email: String,

    @field:NotBlank
    var password: String,

    @field:NotBlank
    @Column(unique = true)
    var phoneNumber: String,

    @Column
    var firstName: String,

    @Column
    var lastName: String,

    @Column
    val birth: LocalDate,

    @Column
    var image: String = "",

    @Column
    var introduction: String = "",

    @Column(columnDefinition = "BINARY(16)")
    val streamId: UUID,

    @OneToMany(mappedBy = "user")
    val posts: MutableList<Post> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    val likes: MutableList<Like> = mutableListOf(),

) : BaseTimeEntity()
