package com.wafflestudio.msns.domain.post.repository

import com.wafflestudio.msns.domain.post.dto.PostResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface PostRepositoryCustom {
    fun getFeed(cursor: String?, pageable: Pageable): Slice<PostResponse.FeedResponse>
}
