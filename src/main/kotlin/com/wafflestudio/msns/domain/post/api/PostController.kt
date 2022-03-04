package com.wafflestudio.msns.domain.post.api

import com.wafflestudio.msns.domain.post.service.PostService
import org.springframework.web.bind.annotation.RestController

@RestController
class PostController(
    private val postService: PostService,
)
