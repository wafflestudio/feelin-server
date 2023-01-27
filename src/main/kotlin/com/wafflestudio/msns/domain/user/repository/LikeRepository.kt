package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.model.Like
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface LikeRepository : JpaRepository<Like, UUID?> {
    fun findAllByPost_Id(pageable: Pageable, postId: UUID): Page<Like>?

    @Transactional
    fun deleteAllByPost_Id(postId: UUID)

    @Transactional
    fun deleteByPost_IdAndUser_Id(postId: UUID, userId: UUID)

    @Transactional
    @Modifying
    @Query("delete from likes l WHERE l.user_id = :userId", nativeQuery = true)
    fun deleteMappingByUserId(@Param("userId") userId: UUID)

    @Transactional
    @Modifying
    @Query(
        "delete from likes l where l.post_id in " +
            "(select p.id from post p where p.user_id = :userId)",
        nativeQuery = true
    )
    fun deleteMappingByUserIdOfPost(@Param("userId") userId: UUID)

    fun countByPost_Id(postId: UUID): Long

    fun existsByPost_IdAndUser_Id(postId: UUID, userId: UUID): Boolean
}
