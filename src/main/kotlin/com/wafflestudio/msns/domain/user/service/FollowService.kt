package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.exception.AlreadyExistFollowException
import com.wafflestudio.msns.domain.user.exception.ForbiddenFollowException
import com.wafflestudio.msns.domain.user.exception.UserNotFoundException
import com.wafflestudio.msns.domain.user.model.Follow
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.BlockRepository
import com.wafflestudio.msns.domain.user.repository.FollowRepository
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.util.CursorUtil
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FollowService(
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository,
    private val blockRepository: BlockRepository
) {
    fun makeFollow(fromUser: User, toUserId: UUID) {
        if (toUserId == fromUser.id) throw ForbiddenFollowException("You cannot follow yourself.")
        userRepository.findByIdOrNull(toUserId)
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

    fun getFollowings(
        loginUser: User,
        cursor: String?,
        pageable: Pageable,
        fromUserId: UUID
    ): ResponseEntity<Slice<UserResponse.FollowListResponse>> {
        if (blockRepository.existsByFromUserAndToUser_Id(loginUser, fromUserId) ||
            blockRepository.existsByFromUser_IdAndToUser(fromUserId, loginUser)
        )
            return ResponseEntity(null, null, HttpStatus.NO_CONTENT)
        val httpHeaders = HttpHeaders()
        val httpBody: Slice<UserResponse.FollowListResponse> =
            followRepository.getFollowsByFromUserId(loginUser, cursor, pageable, fromUserId)
        val lastElement: UserResponse.FollowListResponse? = httpBody.content.lastOrNull()
        val nextCursor: String? = CursorUtil.generateCustomCursor(lastElement?.createdAt)
        httpHeaders.set("cursor", nextCursor)
        return ResponseEntity(httpBody, httpHeaders, HttpStatus.OK)
    }

    fun getFollowers(
        loginUser: User,
        cursor: String?,
        pageable: Pageable,
        toUserId: UUID
    ): ResponseEntity<Slice<UserResponse.FollowListResponse>> {
        if (blockRepository.existsByFromUserAndToUser_Id(loginUser, toUserId) ||
            blockRepository.existsByFromUser_IdAndToUser(toUserId, loginUser)
        )
            return ResponseEntity(null, null, HttpStatus.NO_CONTENT)
        val httpHeaders = HttpHeaders()
        val httpBody: Slice<UserResponse.FollowListResponse> =
            followRepository.getFollowsByToUserId(loginUser, cursor, pageable, toUserId)
        val lastElement: UserResponse.FollowListResponse? = httpBody.content.lastOrNull()
        val nextCursor: String? = CursorUtil.generateCustomCursor(lastElement?.createdAt)
        httpHeaders.set("cursor", nextCursor)
        return ResponseEntity(httpBody, httpHeaders, HttpStatus.OK)
    }

    fun deleteFollow(fromUser: User, toUserId: UUID) =
        followRepository.deleteFollowByFromUserAndToUserId(fromUser, toUserId)
}
