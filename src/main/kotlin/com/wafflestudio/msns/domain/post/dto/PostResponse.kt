package com.wafflestudio.msns.domain.post.dto

import com.wafflestudio.msns.domain.post.model.Post
import com.wafflestudio.msns.domain.user.dto.UserResponse
import java.time.LocalDateTime

class PostResponse {
    data class UserPageResponse (
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
}
