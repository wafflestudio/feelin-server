package com.wafflestudio.msns.domain.user.dto

import com.wafflestudio.msns.domain.user.model.Like
import java.time.LocalDateTime

class LikeResponse {
    data class DetailResponse(
        val user: UserResponse.LikeResponse,
        val createdAt: LocalDateTime
    ) {
        constructor(like: Like) : this (
            user = UserResponse.LikeResponse(like.user),
            createdAt = like.createdAt
        )
    }
}
