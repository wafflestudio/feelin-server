package com.wafflestudio.msns.domain.post.repository

import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.user.model.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface PostCustomRepository {
    fun getFeed(
        loginUser: User,
        viewFollowers: Boolean,
        cursor: String?,
        pageable: Pageable
    ): Slice<PostResponse.FeedResponse>

    fun getAllByUserId(
        userId: UUID,
        cursor: String?,
        pageable: Pageable
    ): Slice<PostResponse.PreviewResponse>
}
