package com.wafflestudio.msns.domain.post.dto

import com.wafflestudio.msns.domain.playlist.dto.PlaylistRequest
import java.util.UUID

class PostRequest {
    data class CreateRequest(
        val title: String,
        val content: String,
        val playlist: PlaylistRequest.PreviewRequest,
    )

    data class PutRequest(
        val title: String,
        val content: String,
        val playlist: PlaylistRequest.PutRequest,
    )

    data class ReportRequest(
        val id: String,
        val title: String,
        val content: String
    )

    data class ActivityRequest(
        val id: UUID,
        val title: String,
        val content: String,
    )
}
