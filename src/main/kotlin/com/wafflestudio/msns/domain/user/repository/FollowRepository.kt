package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.Follow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface FollowRepository : JpaRepository<Follow, Long?> {
    @Query(nativeQuery = true)
    fun findAllWithFromUserId(pageable: Pageable, fromUserId: Long): Page<UserResponse.FollowListResponse>

    @Query(nativeQuery = true)
    fun findAllWithToUserId(pageable: Pageable, toUserId: Long): Page<UserResponse.FollowListResponse>

    @Transactional
    @Modifying
    fun deleteFollowsByFromUser_Id(id: Long)

    @Transactional
    @Modifying
    fun deleteFollowsByToUser_Id(id: Long)
}
