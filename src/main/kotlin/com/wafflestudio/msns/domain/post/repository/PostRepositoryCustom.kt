package com.wafflestudio.msns.domain.post.repository

import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.user.model.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface PostRepositoryCustom {
    fun getFeed(user: User, cursor: String?, pageable: Pageable): Slice<PostResponse.FeedResponse>
}
