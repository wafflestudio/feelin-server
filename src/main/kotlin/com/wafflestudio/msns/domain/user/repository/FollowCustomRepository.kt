package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface FollowCustomRepository {
    fun getFollowsByFromUserId(
        loginUser: User,
        cursor: String?,
        pageable: Pageable,
        fromUserId: UUID
    ): Slice<UserResponse.FollowListResponse>

    fun getFollowsByToUserId(
        loginUser: User,
        cursor: String?,
        pageable: Pageable,
        toUserId: UUID
    ): Slice<UserResponse.FollowListResponse>

    fun deleteFollowsByFromUserId(
        fromUserId: UUID
    )

    fun deleteFollowsByToUserId(
        toUserId: UUID
    )

    fun deleteFollowByFromUserAndToUserId(
        fromUser: User,
        toUserId: UUID
    )

    fun deleteFollowByFromUserIdAndToUser(
        fromUserId: UUID,
        toUser: User
    )
}
