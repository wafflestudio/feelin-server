package com.wafflestudio.msns.domain.playlist.dto

import java.util.UUID

class PlaylistRequest {
    data class PreviewRequest(
        val id: UUID,
        val length: Int,
        val order: String,
        val thumbnail: String
    )

    data class PutRequest(
        val order: String,
        val thumbnail: String
    )
}
