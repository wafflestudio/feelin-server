package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.post.exception.PostNotFoundException
import com.wafflestudio.msns.domain.post.repository.PostRepository
import com.wafflestudio.msns.domain.user.dto.LikeResponse
import com.wafflestudio.msns.domain.user.model.Like
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.LikeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class LikeService(
    private val likeRepository: LikeRepository,
    private val postRepository: PostRepository
) {
    fun writeLike(user: User, postId: Long) {
        postRepository.findPostById(postId)
            ?.let { post -> Like(user = user, post = post) }
            ?.let { newLike -> likeRepository.save(newLike) }
            ?: throw PostNotFoundException("post is not found with the id.")
    }

    fun getLikes(pageable: Pageable, postId: Long): Page<LikeResponse.DetailResponse> =
        likeRepository.findAllByPost_Id(pageable, postId)
            ?.let { it.map { like -> LikeResponse.DetailResponse(like) } }
            ?: throw PostNotFoundException("post is not found with the id.")

    fun deleteLike(user: User, postId: Long) =
        likeRepository.deleteByPost_IdAndUser_Id(postId, user.id)
}
