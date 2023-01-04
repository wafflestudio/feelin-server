package com.wafflestudio.msns.domain.post.repository

import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.model.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface PostRepository : JpaRepository<Post, Long?>, PostRepositoryCustom {
    fun findPostById(id: Long): Post?

    @Query(nativeQuery = true)
    fun findAllWithUserId(pageable: Pageable, @Param("userId") userId: Long): Page<PostResponse.PreviewResponse>

    @Query(nativeQuery = true)
    fun findAllWithMyId(pageable: Pageable, @Param("myId") myId: Long): Page<PostResponse.UserPageResponse>

    @Transactional
    @Modifying
    @Query("DELETE FROM Post p WHERE p.user_id = :userId", nativeQuery = true)
    fun deleteAllUserPosts(@Param("userId") userId: Long)
}
