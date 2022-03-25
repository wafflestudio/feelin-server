package com.wafflestudio.msns.domain.track.dto

import com.wafflestudio.msns.domain.album.dto.AlbumResponse
import com.wafflestudio.msns.domain.track.model.Track

class TrackResponse {
    data class PostDetailResponse(
        val id: Long,
        val title: String,
        val album: AlbumResponse.SimpleResponse
    ) {
        constructor(track: Track) : this(
            id = track.id,
            title = track.title,
            album = AlbumResponse.SimpleResponse(track.album)
        )
    }
}
