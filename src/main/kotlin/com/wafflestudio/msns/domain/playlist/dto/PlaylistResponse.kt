package com.wafflestudio.msns.domain.playlist.dto

import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.track.dto.TrackResponse

class PlaylistResponse {
    data class DetailResponse(
        val id: Long,
        val thumbnail: String,
        val title: String,
        val tracks: List<TrackResponse.DetailResponse>
    ) {
        constructor(playlist: Playlist) : this(
            id = playlist.id,
            thumbnail = playlist.thumbnail,
            title = playlist.title,
            tracks = playlist.tracks.map { TrackResponse.DetailResponse(it) }
        )
    }

    data class APIResponse(
        val message: String,
        val playlist: DetailResponse
    )

}
