package com.wafflestudio.msns.domain.post.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.user.model.User
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
@Table(
    name = "post",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "playlist_id"])]
)
class Post(
    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    var title: String,

    var content: String,

    @OneToOne
    @JoinColumn(name = "playlist_id", referencedColumnName = "id")
    val playlist: Playlist,

) : BaseTimeEntity()
