package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.model.Follow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.transaction.annotation.Transactional

interface FollowRepository : JpaRepository<Follow, Long?>, FollowRepositoryCustom {
    @Transactional
    @Modifying
    fun deleteFollowsByFromUser_Id(id: Long)

    @Transactional
    @Modifying
    fun deleteFollowsByToUser_Id(id: Long)

    @Transactional
    @Modifying
    fun deleteFollowByFromUser_IdAndToUser_Id(fromUserId: Long, toUserId: Long)

    fun countByFromUser_Id(fromUserId: Long): Long

    fun countByToUser_Id(toUserId: Long): Long

    fun existsByFromUser_IdAndToUser_Id(fromUserId: Long, toUserId: Long): Boolean
}
