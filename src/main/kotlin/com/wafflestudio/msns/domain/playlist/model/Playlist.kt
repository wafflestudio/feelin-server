package com.wafflestudio.msns.domain.playlist.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.user.model.User
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.CascadeType

@Entity
@Table(
    name = "playlist",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "title"])]
)
class Playlist(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    val title: String,

    @OneToMany(mappedBy = "playlist", cascade = [CascadeType.ALL])
    val tracks: MutableList<PlaylistTrack> = mutableListOf(),

    val thumbnail: String = "",

) : BaseTimeEntity()
