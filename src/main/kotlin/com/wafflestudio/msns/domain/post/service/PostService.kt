package com.wafflestudio.msns.domain.post.service

import com.wafflestudio.msns.domain.post.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostService(
    private val postRepository: PostRepository,
)
