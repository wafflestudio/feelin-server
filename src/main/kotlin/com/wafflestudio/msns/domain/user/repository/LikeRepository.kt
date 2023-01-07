package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.model.Like
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface LikeRepository : JpaRepository<Like, Long?> {
    fun findAllByPost_Id(pageable: Pageable, postId: Long): Page<Like>?

    @Transactional
    fun deleteAllByPost_Id(postId: Long)

    @Transactional
    fun deleteByPost_IdAndUser_Id(postId: Long, userId: Long)

    @Transactional
    @Modifying
    @Query("DELETE FROM Likes l WHERE l.user_id = :userId", nativeQuery = true)
    fun deleteMappingByUserId(@Param("userId") userId: Long)

    @Transactional
    @Modifying
    @Query(
        "DELETE FROM Likes l WHERE l.post_id in " +
            "(SELECT p.id FROM Post p WHERE p.user_id = :userId)",
        nativeQuery = true
    )
    fun deleteMappingByUserIdOfPost(@Param("userId") userId: Long)

    fun countByPost_Id(postId: Long): Long

    fun existsByPost_IdAndUser_Id(postId: Long, userId: Long): Boolean
}
