package com.wafflestudio.msns.domain.post.repository

import com.wafflestudio.msns.domain.post.model.Post
import com.wafflestudio.msns.domain.user.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PostRepository : JpaRepository<Post, Long?> {
    fun findPostById(id: Long): Post?

    fun findByUser_IdAndPlaylist_Id(userId: Long, playlistId: Long): Post?

    fun findAllByUser(pageable: Pageable, user: User): Page<Post>

    @Query("SELECT p FROM Post p WHERE p.user.id = :userId")
    fun findUserPosts(pageable: Pageable, @Param("userId") userId: Long): Page<Post>
}
