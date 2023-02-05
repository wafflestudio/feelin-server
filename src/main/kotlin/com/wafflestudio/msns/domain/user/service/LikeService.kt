package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.post.exception.PostNotFoundException
import com.wafflestudio.msns.domain.post.repository.PostRepository
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.Like
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.BlockRepository
import com.wafflestudio.msns.domain.user.repository.LikeRepository
import com.wafflestudio.msns.global.util.CursorUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LikeService(
    private val likeRepository: LikeRepository,
    private val postRepository: PostRepository,
    private val blockRepository: BlockRepository
) {
    fun writeLike(user: User, postId: UUID) {
        postRepository.findPostById(postId)
            ?.let { post -> Like(user = user, post = post) }
            ?.let { newLike -> likeRepository.save(newLike) }
            ?: throw PostNotFoundException("post is not found with the id.")
    }

    fun getLikes(
        loginUser: User,
        cursor: String?,
        pageable: Pageable,
        postId: UUID
    ): ResponseEntity<Slice<UserResponse.LikeListResponse>> {
        postRepository.findPostById(postId)
            ?.also { post ->
                if (blockRepository.existsByFromUserAndToUser(loginUser, post.user) ||
                    blockRepository.existsByFromUserAndToUser(post.user, loginUser)
                )
                    return ResponseEntity(null, null, HttpStatus.FORBIDDEN)
            }
            ?: throw PostNotFoundException("post is not found with the id.")
        val httpHeaders = HttpHeaders()
        val httpBody: Slice<UserResponse.LikeListResponse> =
            likeRepository.getLikes(loginUser, cursor, pageable, postId)
        val lastElement: UserResponse.LikeListResponse? = httpBody.content.lastOrNull()
        val nextCursor: String? = CursorUtil.generateCustomCursor(lastElement?.createdAt)
        httpHeaders.set("cursor", nextCursor)
        return ResponseEntity(httpBody, httpHeaders, HttpStatus.OK)
    }

    fun deleteLike(user: User, postId: UUID) =
        likeRepository.deleteByPost_IdAndUser_Id(postId, user.id)
}
