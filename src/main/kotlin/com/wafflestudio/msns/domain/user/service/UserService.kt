package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.playlist.repository.PlaylistRepository
import com.wafflestudio.msns.domain.post.repository.PostRepository
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.exception.UserNotFoundException
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.FollowRepository
import com.wafflestudio.msns.domain.user.repository.LikeRepository
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.exception.ForbiddenUsernameException
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import org.springframework.data.repository.findByIdOrNull
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
    private val playlistRepository: PlaylistRepository
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

    fun getProfileByUserId(loginUser: User, id: UUID): UserResponse.ProfileResponse =
        userRepository.findByIdOrNull(id)
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
            ?: throw UserNotFoundException("user is not found with the userId.")

    fun putMyProfile(user: User, putRequest: UserRequest.PutProfile): UserResponse.MyProfileResponse =
        user.apply {
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

    fun withdrawUser(user: User) =
        run {
            followRepository.deleteFollowsByFromUser_Id(user.id)
            followRepository.deleteFollowsByToUser_Id(user.id)
            likeRepository.deleteMappingByUserId(user.id)
            likeRepository.deleteMappingByUserIdOfPost(user.id)

            postRepository.deleteAllUserPosts(user.id)
            playlistRepository.deleteMappingByUserId(user.id)
            verificationTokenRepository.deleteVerificationTokenByEmailOrPhoneNumber(user.email, user.phoneNumber)

            userRepository.deleteUserById(user.id)
        }
}
