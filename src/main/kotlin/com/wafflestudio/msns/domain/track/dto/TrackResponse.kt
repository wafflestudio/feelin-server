package com.wafflestudio.msns.domain.track.dto

import com.wafflestudio.msns.domain.album.dto.AlbumResponse
import com.wafflestudio.msns.domain.playlist.model.PlaylistTrack
import com.wafflestudio.msns.domain.track.model.Track

class TrackResponse {
    data class PostDetailResponse(
        val id: Long,
        val title: String,
        val album: AlbumResponse.SimpleResponse
    ) {
        constructor(playlistTrack: PlaylistTrack) : this(
            id = playlistTrack.track.id,
            title = playlistTrack.track.title,
            album = AlbumResponse.SimpleResponse(playlistTrack.track.album)
        )
    }
}
