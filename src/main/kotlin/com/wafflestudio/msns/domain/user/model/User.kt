package com.wafflestudio.msns.domain.user.model

import com.wafflestudio.msns.domain.model.BaseEntity
import com.wafflestudio.msns.domain.post.model.Post
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
@Table(
    name = "user",
    uniqueConstraints = arrayOf(
        UniqueConstraint(columnNames = arrayOf("username", "email"))
    )
)
class User(
    @field:NotBlank
    val username: String,

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    var password: String,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [])
    val posts: MutableList<Post> = mutableListOf(),

) : BaseEntity()
