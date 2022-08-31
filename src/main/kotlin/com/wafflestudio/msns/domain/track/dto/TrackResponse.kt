package com.wafflestudio.msns.domain.track.dto

import com.wafflestudio.msns.domain.album.dto.AlbumResponse

class TrackResponse {
    data class PostDetailResponse(
        val id: Long,
        val title: String,
        val album: AlbumResponse.SimpleResponse
    )
}
