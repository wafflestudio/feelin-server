package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.model.Follow
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FollowRepository : JpaRepository<Follow, UUID?>, FollowCustomRepository {
    fun countByFromUser_Id(fromUserId: UUID): Long

    fun countByToUser_Id(toUserId: UUID): Long

    fun existsByFromUser_IdAndToUser_Id(fromUserId: UUID, toUserId: UUID): Boolean
}
