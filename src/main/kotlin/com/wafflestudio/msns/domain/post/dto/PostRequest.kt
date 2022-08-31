package com.wafflestudio.msns.domain.post.dto

import com.wafflestudio.msns.domain.playlist.dto.PlaylistRequest

class PostRequest {
    data class CreateRequest(
        val title: String,
        val content: String,
        val playlistPreview: PlaylistRequest.PreviewRequest,
    )

    data class PutRequest(
        val title: String,
        val content: String
    )
}
