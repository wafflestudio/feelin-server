package com.wafflestudio.msns.domain.post.dto

import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import com.wafflestudio.msns.domain.post.model.Post
import com.wafflestudio.msns.domain.user.dto.UserResponse
import java.time.LocalDateTime
import java.util.UUID

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
        val playlist: PlaylistResponse.PreviewResponse,
        val likes: Int
    ) {
        constructor(post: Post) : this(
            id = post.id,
            title = post.title,
            content = post.content,
            createdAt = post.createdAt,
            playlist = PlaylistResponse.PreviewResponse(post.playlist),
            likes = post.likes.size
        )
        constructor(
            post_id: Long,
            title: String,
            content: String,
            createdAt: LocalDateTime?,
            playlist_id: UUID,
            thumbnail: String,
            likes: Int
        ) : this(
            id = post_id,
            title = title,
            content = content,
            createdAt = createdAt,
            playlist = PlaylistResponse.PreviewResponse(playlist_id, thumbnail),
            likes = likes
        )
    }

    data class DetailResponse(
        val id: Long,
        val title: String,
        val content: String,
        val user: UserResponse.PostResponse,
        val createdAt: LocalDateTime?,
        val playlist: PlaylistResponse.DetailResponse,
        val likes: Int,
    ) {
        constructor(post: Post, playlist: PlaylistResponse.DetailResponse) : this(
            id = post.id,
            title = post.title,
            content = post.content,
            user = UserResponse.PostResponse(post.user),
            createdAt = post.createdAt,
            playlist = playlist,
            likes = post.likes.size
        )
    }

    data class PlaylistOrderResponse(
        val order: String
    )
}
