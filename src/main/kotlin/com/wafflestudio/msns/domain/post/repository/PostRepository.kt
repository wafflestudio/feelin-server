package com.wafflestudio.msns.domain.post.repository

import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.model.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PostRepository : JpaRepository<Post, Long?> {
    fun findPostById(id: Long): Post?

    fun findByUser_IdAndPlaylist_Id(userId: Long, playlistId: Long): Post?

    @Query(nativeQuery = true)
    fun findAllWithUserId(pageable: Pageable, @Param("userId") userId: Long): Page<PostResponse.PreviewResponse>

    @Query(nativeQuery = true)
    fun findAllWithMyId(pageable: Pageable, @Param("myId") myId: Long): Page<PostResponse.UserPageResponse>
}
