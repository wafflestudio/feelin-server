package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface FollowCustomRepository {
    fun getFollowingsByFromUserId(
        loginUser: User,
        cursor: String?,
        pageable: Pageable,
        fromUserId: UUID
    ): Slice<UserResponse.FollowListResponse>
    fun getFollowingsByToUserId(
        loginUser: User,
        cursor: String?,
        pageable: Pageable,
        toUserId: UUID
    ): Slice<UserResponse.FollowListResponse>
}
