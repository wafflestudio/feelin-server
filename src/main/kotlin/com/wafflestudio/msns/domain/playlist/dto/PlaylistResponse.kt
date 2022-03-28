package com.wafflestudio.msns.domain.playlist.dto

import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.playlist.model.PlaylistTrack
import com.wafflestudio.msns.domain.track.dto.TrackResponse

class PlaylistResponse {
    data class PostDetailResponse(
        val id: Long,
        val thumbnail: String,
        val tracks: List<TrackResponse.PostDetailResponse>
    ) {
        constructor(playlist: Playlist) : this(
            id = playlist.id,
            thumbnail = playlist.thumbnail,
            tracks = playlist.tracks.map { TrackResponse.PostDetailResponse(it) }
        )
    }
}
