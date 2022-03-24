package com.wafflestudio.msns.domain.playlist.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.track.model.Track
import com.wafflestudio.msns.domain.user.model.User
import javax.persistence.*

@Entity
@Table(
    name = "playlist",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "title"])]
)
class Playlist (
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    val title: String,

    @ManyToMany(fetch = FetchType.LAZY)
    val tracks: MutableList<Track> = mutableListOf(),

    val thumbnail: String,

): BaseTimeEntity()
