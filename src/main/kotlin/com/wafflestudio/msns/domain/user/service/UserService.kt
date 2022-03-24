package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.repository.PostRepository
import com.wafflestudio.msns.domain.user.exception.UserNotFoundException
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) {
    fun getPosts(pageable: Pageable, email: String): Page<PostResponse.UserPageResponse> {
        return userRepository.findByEmail(email)
            ?.let { user -> postRepository.findAllByUser(pageable, user) }
            ?.map { post -> PostResponse.UserPageResponse(post) }
            ?: throw UserNotFoundException("user not found with the email and username.")
    }
}
