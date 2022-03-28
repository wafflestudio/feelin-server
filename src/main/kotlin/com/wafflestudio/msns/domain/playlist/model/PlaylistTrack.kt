package com.wafflestudio.msns.domain.playlist.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.track.model.Track
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.persistence.JoinColumn

@Entity
@Table(
    name = "playlist_track",
    uniqueConstraints = [UniqueConstraint(columnNames = ["playlist_id", "track_id"])]
)
class PlaylistTrack(
    @ManyToOne
    @JoinColumn(name = "playlist_id", referencedColumnName = "id")
    val playlist: Playlist,

    @ManyToOne
    @JoinColumn(name = "track_id", referencedColumnName = "id")
    val track: Track,

) : BaseTimeEntity()
