package com.wafflestudio.msns.domain.user.model

import com.wafflestudio.msns.domain.model.BaseEntity
import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.post.model.Post
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "user")
class User(
    @field:NotBlank
    val username: String,

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
    var firstName: String? = null,

    @Column
    var lastName: String? = null,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [])
    val posts: MutableList<Post> = mutableListOf(),

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [])
    val playlists: MutableList<Playlist> = mutableListOf(),

) : BaseEntity()
