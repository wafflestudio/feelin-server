package com.wafflestudio.msns.domain.playlist.dto

import java.util.UUID

class PlaylistRequest {
    data class PreviewRequest(
        val id: UUID,
        val thumbnail: String
        )
}
