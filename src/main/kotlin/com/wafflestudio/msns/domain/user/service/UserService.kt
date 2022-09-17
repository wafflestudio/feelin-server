package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.repository.PostRepository
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.exception.UserNotFoundException
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.exception.ForbiddenUsernameException
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
    fun getMyself(user: User): UserResponse.DetailResponse = UserResponse.DetailResponse(user)

    fun getProfile(userId: Long): UserResponse.ProfileResponse =
        userRepository.findByIdOrNull(userId)
            ?.let { user -> UserResponse.ProfileResponse(user) }
            ?: throw UserNotFoundException("user is not found with the userId.")

    fun putProfile(user: User, putRequest: UserRequest.PutProfile): UserResponse.ProfileResponse =
        user.apply {
            this.username = putRequest.username.also {
                if ((userRepository.findByUsername(it)?.id ?: user.id) != user.id)
                    throw ForbiddenUsernameException("There is another user using this name.")
            }
            this.image = putRequest.image
            this.introduction = putRequest.introduction
        }
            .let { userRepository.save(it) }
            .let { UserResponse.ProfileResponse(it) }

    fun getPosts(pageable: Pageable, userId: Long): Page<PostResponse.UserPageResponse> {
        return userRepository.findByIdOrNull(userId)
            ?.let { user -> postRepository.findAllByUser(pageable, user) }
            ?.map { post -> PostResponse.UserPageResponse(post) }
            ?: throw UserNotFoundException("user is not found with the userId.")
    }
}
