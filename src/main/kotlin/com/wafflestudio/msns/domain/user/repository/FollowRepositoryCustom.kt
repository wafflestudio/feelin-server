package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.dto.UserResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FollowRepositoryCustom {
    fun getFollowingsByFromUserId(pageable: Pageable, fromUserId: Long): Page<UserResponse.FollowListResponse>
    fun getFollowingsByToUserId(pageable: Pageable, toUserId: Long): Page<UserResponse.FollowListResponse>
}
