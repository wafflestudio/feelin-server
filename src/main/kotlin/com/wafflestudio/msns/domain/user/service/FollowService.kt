package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.exception.AlreadyExistFollowException
import com.wafflestudio.msns.domain.user.exception.ForbiddenFollowException
import com.wafflestudio.msns.domain.user.exception.UserNotFoundException
import com.wafflestudio.msns.domain.user.model.Follow
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.FollowRepository
import com.wafflestudio.msns.domain.user.repository.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FollowService(
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository
) {
    fun makeFollow(fromUser: User, toUserId: UUID) {
        userRepository.findByIdOrNull(toUserId)
            ?.also { if (toUserId == fromUser.id) throw ForbiddenFollowException("You cannot follow yourself.") }
            ?.let { toUser ->
                try {
                    followRepository.save(
                        Follow(
                            toUser = toUser,
                            fromUser = fromUser
                        )
                    )
                } catch (e: DataIntegrityViolationException) {
                    throw AlreadyExistFollowException("You already followed the user.")
                }
            }
            ?: throw UserNotFoundException("The user you want to follow is not found.")
    }

    fun getFollowings(pageable: Pageable, fromUserId: UUID): Page<UserResponse.FollowListResponse> =
        followRepository.getFollowingsByFromUserId(pageable, fromUserId)

    fun getFollowers(pageable: Pageable, toUserId: UUID): Page<UserResponse.FollowListResponse> =
        followRepository.getFollowingsByToUserId(pageable, toUserId)

    fun deleteFollow(fromUser: User, toUserId: UUID) =
        followRepository.deleteFollowByFromUser_IdAndToUser_Id(fromUser.id, toUserId)
}
