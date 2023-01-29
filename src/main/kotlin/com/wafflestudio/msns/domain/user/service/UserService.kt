package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.playlist.repository.PlaylistRepository
import com.wafflestudio.msns.domain.playlist.service.PlaylistClientService
import com.wafflestudio.msns.domain.post.repository.PostRepository
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.exception.UserNotFoundException
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.BlockRepository
import com.wafflestudio.msns.domain.user.repository.FollowRepository
import com.wafflestudio.msns.domain.user.repository.LikeRepository
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.exception.ForbiddenUsernameException
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val followRepository: FollowRepository,
    private val likeRepository: LikeRepository,
    private val blockRepository: BlockRepository,
    private val playlistRepository: PlaylistRepository,
    private val playlistClientService: PlaylistClientService
) {
    fun getMyself(user: User): UserResponse.DetailResponse =
        UserResponse.DetailResponse(
            user.id,
            user.email,
            user.username,
            user.phoneNumber,
            user.name,
            user.birthDate
        )

    fun getMyProfile(user: User): UserResponse.MyProfileResponse =
        UserResponse.MyProfileResponse(
            user.id,
            user.username,
            user.name,
            user.profileImageUrl,
            user.introduction,
            postRepository.countByUser_Id(user.id),
            followRepository.countByToUser_Id(user.id),
            followRepository.countByFromUser_Id(user.id)
        )

    fun getProfileByUserId(loginUser: User, id: UUID): ResponseEntity<UserResponse.ProfileResponse> =
        userRepository.findByIdOrNull(id)
            ?.also { user ->
                if (blockRepository.existsByFromUserAndToUser(loginUser, user) ||
                    blockRepository.existsByFromUserAndToUser(user, loginUser)
                )
                    return ResponseEntity(null, null, HttpStatus.NO_CONTENT)
            }
            ?.let { user ->
                UserResponse.ProfileResponse(
                    user.id,
                    user.username,
                    user.name,
                    user.profileImageUrl,
                    user.introduction,
                    postRepository.countByUser_Id(id),
                    followRepository.countByToUser_Id(id),
                    followRepository.countByFromUser_Id(id),
                    followRepository.existsByFromUser_IdAndToUser_Id(loginUser.id, id)
                )
            }
            ?.let { httpBody -> ResponseEntity(httpBody, null, HttpStatus.NO_CONTENT) }
            ?: throw UserNotFoundException("user is not found with the userId.")

    fun putMyProfile(user: User, putRequest: UserRequest.PutProfile): UserResponse.MyProfileResponse =
        user.apply {
            this.name = putRequest.name
            this.username = putRequest.username.also {
                if ((userRepository.findByUsername(it)?.id ?: user.id) != user.id)
                    throw ForbiddenUsernameException("There is another user using this name.")
            }
            this.profileImageUrl = putRequest.profileImageUrl
            this.introduction = putRequest.introduction
        }
            .let { userRepository.save(it) }
            .let {
                UserResponse.MyProfileResponse(
                    user.id,
                    user.username,
                    user.name,
                    user.profileImageUrl,
                    user.introduction,
                    postRepository.countByUser_Id(user.id),
                    followRepository.countByToUser_Id(user.id),
                    followRepository.countByFromUser_Id(user.id)
                )
            }

    @Transactional
    suspend fun withdrawUser(user: User, accessToken: String) {
        followRepository.deleteFollowsByFromUserId(user.id)
        followRepository.deleteFollowsByToUserId(user.id)
        likeRepository.deleteMappingByUserId(user.id.toString())
        likeRepository.deleteMappingByUserIdOfPost(user.id.toString())

        postRepository.deleteAllByUserId(user.id)
        playlistRepository.deleteAllByUserId(user.id)
        verificationTokenRepository.deleteVerificationTokenByEmailOrPhoneNumberAndCountryCode(
            user.email,
            user.phoneNumber,
            user.countryCode
        )

        userRepository.deleteUserById(user.id)
        playlistClientService.withdrawUser(user.id, accessToken)
    }
}
