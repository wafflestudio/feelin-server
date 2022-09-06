package com.wafflestudio.msns.domain.track.dto

import com.wafflestudio.msns.domain.album.dto.AlbumResponse
import com.wafflestudio.msns.domain.artist.dto.ArtistResponse
import java.util.UUID

class TrackResponse {
    data class APIDto(
        val id: UUID,
        val title: String,
        val artists: List<ArtistResponse.APIDto>,
        val album: AlbumResponse.APIDto
    )
}
