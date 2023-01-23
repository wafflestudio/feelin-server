package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.model.Follow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface FollowRepository : JpaRepository<Follow, Long?>, FollowCustomRepository {
    @Transactional
    @Modifying
    fun deleteFollowsByFromUser_Id(id: UUID)

    @Transactional
    @Modifying
    fun deleteFollowsByToUser_Id(id: UUID)

    @Transactional
    @Modifying
    fun deleteFollowByFromUser_IdAndToUser_Id(fromUserId: UUID, toUserId: UUID)

    fun countByFromUser_Id(fromUserId: UUID): Long

    fun countByToUser_Id(toUserId: UUID): Long

    fun existsByFromUser_IdAndToUser_Id(fromUserId: UUID, toUserId: UUID): Boolean
}
