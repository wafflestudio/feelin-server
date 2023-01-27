package com.wafflestudio.msns.domain.post.repository

import com.wafflestudio.msns.domain.post.model.Post
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PostRepository : JpaRepository<Post, UUID?>, PostCustomRepository {
    fun findPostById(id: UUID): Post?

    fun countByUser_Id(userId: UUID): Long
}
