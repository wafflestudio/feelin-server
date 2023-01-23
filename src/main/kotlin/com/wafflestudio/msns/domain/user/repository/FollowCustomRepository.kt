package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.dto.UserResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface FollowCustomRepository {
    fun getFollowingsByFromUserId(pageable: Pageable, fromUserId: UUID): Page<UserResponse.FollowListResponse>
    fun getFollowingsByToUserId(pageable: Pageable, toUserId: UUID): Page<UserResponse.FollowListResponse>
}
