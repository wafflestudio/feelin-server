package com.wafflestudio.msns.domain.post.repository

import com.wafflestudio.msns.domain.post.model.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long?>
