package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.dto.LikeResponse
import com.wafflestudio.msns.domain.user.model.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface LikeCustomRepository {
    fun getLikes(
        loginUser: User,
        cursor: String?,
        pageable: Pageable,
        postId: UUID
    ): Slice<LikeResponse.DetailResponse>
}
