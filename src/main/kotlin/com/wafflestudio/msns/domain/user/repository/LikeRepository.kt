package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.model.Like
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

interface LikeRepository : JpaRepository<Like, Long?> {
    fun findAllByPost_Id(pageable: Pageable, postId: Long): Page<Like>?

    @Transactional
    fun deleteAllByPost_Id(postId: Long)

    @Transactional
    fun deleteByPost_IdAndUser_Id(postId: Long, userId: Long)
}
