package com.wafflestudio.msns.domain.post.repository

import com.wafflestudio.msns.domain.post.model.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface PostRepository : JpaRepository<Post, Long?>, PostCustomRepository {
    fun findPostById(id: Long): Post?

    @Transactional
    @Modifying
    @Query("DELETE FROM Post p WHERE p.user_id = :userId", nativeQuery = true)
    fun deleteAllUserPosts(@Param("userId") userId: Long)

    fun countByUser_Id(userId: Long): Long
}
