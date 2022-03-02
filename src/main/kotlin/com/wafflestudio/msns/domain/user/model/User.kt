package com.wafflestudio.msns.domain.user.model

import com.wafflestudio.msns.domain.artist.model.Artist
import com.wafflestudio.msns.domain.model.BaseEntity
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint
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
    val email: String,

    @field:NotBlank
    var password: String,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val artist: Artist? = null,

) : BaseEntity()
