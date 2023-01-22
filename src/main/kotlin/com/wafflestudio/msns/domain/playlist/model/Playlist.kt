package com.wafflestudio.msns.domain.playlist.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.user.model.User
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.Column
import javax.persistence.Index

@Entity
@Table(
    name = "playlist",
    indexes = [
        Index(
            name = "unique_idx_pi",
            columnList = "playlist_id",
            unique = true
        )
    ]
)
class Playlist(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    @Column(name = "playlist_id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    val playlistId: UUID,

    @Column(name = "playlist_order", columnDefinition = "MEDIUMTEXT")
    var playlistOrder: String,

    val thumbnail: String,

) : BaseTimeEntity()
