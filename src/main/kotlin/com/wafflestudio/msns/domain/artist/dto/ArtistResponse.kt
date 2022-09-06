package com.wafflestudio.msns.domain.artist.dto

import java.util.UUID

class ArtistResponse {
    data class APIDto(
        val id: UUID,
        val name: String
    )
}
