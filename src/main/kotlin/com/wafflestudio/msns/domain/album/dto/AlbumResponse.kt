package com.wafflestudio.msns.domain.album.dto

import java.util.UUID

class AlbumResponse {
    data class APIDto(
        val id: UUID,
        val title: String,
        val thumbnail: String
    )
}
