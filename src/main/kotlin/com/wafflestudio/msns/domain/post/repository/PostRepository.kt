package com.wafflestudio.msns.domain.post.repository

import com.wafflestudio.msns.domain.post.model.Post
import com.wafflestudio.msns.domain.user.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long?> {
    fun findByUser_IdAndPlaylist_Id(userId: Long, playlistId: Long): Post?

    fun findByUser_IdAndPlaylist_Title(userId: Long, playlistTitle: String): Post?

    fun findAllByUser(pageable: Pageable, user: User): Page<Post>
}
