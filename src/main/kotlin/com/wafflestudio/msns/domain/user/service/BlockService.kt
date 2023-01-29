package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.exception.AlreadyExistFollowException
import com.wafflestudio.msns.domain.user.exception.ForbiddenBlockException
import com.wafflestudio.msns.domain.user.exception.UserNotFoundException
import com.wafflestudio.msns.domain.user.model.Block
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
class BlockService(
    private val blockRepository: BlockRepository,
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {
    fun blockUser(fromUser: User, toUserId: UUID) {
        if (toUserId == fromUser.id) throw ForbiddenBlockException("You cannot block yourself.")
        followRepository.deleteFollowByFromUserAndToUserId(fromUser = fromUser, toUserId = toUserId)
        followRepository.deleteFollowByFromUserIdAndToUser(fromUserId = toUserId, toUser = fromUser)
        userRepository.findByIdOrNull(toUserId)
            ?.let { toUser ->
                try {
                    blockRepository.save(
                        Block(
                            toUser = toUser,
                            fromUser = fromUser
                        )
                    )
                } catch (e: DataIntegrityViolationException) {
                    throw AlreadyExistFollowException("You already blocked the user.")
                }
            }
            ?: throw UserNotFoundException("The user you want to block is not found.")
    }

    fun getBlocks(
        user: User,
        cursor: String?,
        pageable: Pageable
    ): ResponseEntity<Slice<UserResponse.BlockListResponse>> {
        val httpHeaders = HttpHeaders()
        val httpBody: Slice<UserResponse.BlockListResponse> =
            blockRepository.getBlocksByUser(user, cursor, pageable)
        val lastElement: UserResponse.BlockListResponse? = httpBody.content.lastOrNull()
        val nextCursor: String? = CursorUtil.generateCustomCursor(lastElement?.createdAt)
        httpHeaders.set("cursor", nextCursor)
        return ResponseEntity(httpBody, httpHeaders, HttpStatus.OK)
    }

    fun unBlock(fromUser: User, toUserId: UUID) =
        blockRepository.deleteBlockByFromUserAndToUserId(fromUser, toUserId)
}
