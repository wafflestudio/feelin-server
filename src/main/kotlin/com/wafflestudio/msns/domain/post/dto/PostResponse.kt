package com.wafflestudio.msns.domain.post.dto

import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import com.wafflestudio.msns.domain.post.model.Post
import com.wafflestudio.msns.domain.user.dto.UserResponse
import java.time.LocalDateTime
import java.util.UUID

class PostResponse {
    data class PreviewResponse(
        val id: UUID,
        val title: String,
        val content: String,
        val createdAt: LocalDateTime?,
        val updatedAt: LocalDateTime?,
        val playlist: PlaylistResponse.PreviewResponse,
        val likes: Int
    ) {
        constructor(post: Post) : this(
            id = post.id,
            title = post.title,
            content = post.content,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt,
            playlist = PlaylistResponse.PreviewResponse(post.playlist),
            likes = post.likes.size
        )
        constructor(
            post_id: UUID,
            title: String,
            content: String,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
            playlist_id: UUID,
            thumbnail: String,
            likes: Int
        ) : this(
            id = post_id,
            title = title,
            content = content,
            createdAt = createdAt,
            updatedAt = updatedAt,
            playlist = PlaylistResponse.PreviewResponse(playlist_id, thumbnail),
            likes = likes
        )
    }

    data class DetailResponse(
        val id: UUID,
        val title: String,
        val content: String,
        val user: UserResponse.PreviewResponse,
        val createdAt: LocalDateTime?,
        val updatedAt: LocalDateTime?,
        val playlist: PlaylistResponse.DetailResponse,
        val likes: Long,
        val doesLike: Boolean
    )

    data class PlaylistOrderResponse(
        val order: String
    )

    data class FeedResponse(
        val id: UUID,
        val title: String,
        val content: String,
        val user: UserResponse.PreviewResponse,
        val updatedAt: LocalDateTime?,
        val createdAt: LocalDateTime?,
        val playlist: PlaylistResponse.PreviewResponse,
        val likes: Int,
        val doesLike: Boolean
    )

    data class CreateResponse(
        val id: UUID,
        val title: String,
        val content: String,
        val thumbnail: String
    )
}
