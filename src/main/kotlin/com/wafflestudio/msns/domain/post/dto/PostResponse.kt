package com.wafflestudio.msns.domain.post.dto

import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import com.wafflestudio.msns.domain.post.model.Post
import java.time.LocalDateTime

class PostResponse {
    data class UserPageResponse(
        val id: Long,
        val title: String,
        val createdAt: LocalDateTime?,
        val thumbnail: String,
    ) {
        constructor(post: Post) : this(
            id = post.id,
            title = post.title,
            createdAt = post.createdAt,
            thumbnail = post.playlist.thumbnail,
        )
    }

    data class PreviewResponse(
        val id: Long,
        val title: String,
        val content: String,
        val createdAt: LocalDateTime?,
        val playlist: PlaylistResponse.PreviewResponse
    ) {
        constructor(post: Post) : this(
            id = post.id,
            title = post.title,
            content = post.content,
            createdAt = post.createdAt,
            playlist = PlaylistResponse.PreviewResponse(post.playlist)
        )
    }

    data class DetailResponse(
        val id: Long,
        val title: String,
        val content: String,
        val createdAt: LocalDateTime?,
        val playlist: PlaylistResponse.DetailResponse
    ) {
        constructor(post: Post, playlist: PlaylistResponse.DetailResponse) : this(
            id = post.id,
            title = post.title,
            content = post.content,
            createdAt = post.createdAt,
            playlist = playlist
        )
    }
}
