package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.repository.PostRepository
import com.wafflestudio.msns.domain.user.exception.UserNotFoundException
import com.wafflestudio.msns.domain.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) {
    fun getPosts(pageable: Pageable, userId: Long): Page<PostResponse.UserPageResponse> {
        return userRepository.findByIdOrNull(userId)
            ?.let { user -> postRepository.findAllByUser(pageable, user) }
            ?.map { post -> PostResponse.UserPageResponse(post) }
            ?: throw UserNotFoundException("user is not found with the email.")
    }
}
