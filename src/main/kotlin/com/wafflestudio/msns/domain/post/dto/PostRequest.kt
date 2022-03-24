package com.wafflestudio.msns.domain.post.dto

class PostRequest {
    data class CreateRequest(
        val title: String,
        val content: String,
        val playlistTitle: String,
    )
}
