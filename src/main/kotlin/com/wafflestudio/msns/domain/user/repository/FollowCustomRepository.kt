package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.dto.UserResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface FollowCustomRepository {
    fun getFollowingsByFromUserId(
        cursor: String?,
        pageable: Pageable,
        fromUserId: UUID
    ): Slice<UserResponse.FollowListResponse>
    fun getFollowingsByToUserId(
        cursor: String?,
        pageable: Pageable,
        toUserId: UUID
    ): Slice<UserResponse.FollowListResponse>
}
