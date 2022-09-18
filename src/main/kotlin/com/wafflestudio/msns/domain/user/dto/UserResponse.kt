package com.wafflestudio.msns.domain.user.dto

import com.wafflestudio.msns.domain.user.model.User
import java.time.LocalDate
import java.util.UUID

class UserResponse {
    data class SimpleUserInfo(
        val id: Long,
        val email: String,
        val username: String,
        val phoneNumber: String,
    ) {
        constructor(user: User) : this(
            id = user.id,
            email = user.email,
            username = user.username,
            phoneNumber = user.phoneNumber,
        )
    }

    data class DetailResponse(
        val id: Long,
        val email: String,
        val username: String,
        val phoneNumber: String,
        val firstName: String,
        val lastName: String,
        val birth: LocalDate
    ) {
        constructor(user: User) : this(
            id = user.id,
            email = user.email,
            username = user.username,
            phoneNumber = user.phoneNumber,
            firstName = user.firstName,
            lastName = user.lastName,
            birth = user.birth
        )
    }

    data class ProfileResponse(
        val id: Long,
        val username: String,
        val image: String,
        val introduction: String,
        val countPosts: Int
    ) {
        constructor(user: User) : this(
            id = user.id,
            username = user.username,
            image = user.image,
            introduction = user.introduction,
            countPosts = user.posts.size
        )
    }

    data class PostResponse(
        val id: Long,
        val username: String,
        val image: String
    ) {
        constructor(user: User) : this(
            id = user.id,
            username = user.username,
            image = user.image
        )
    }

    data class PostAPIDto(
        val id: UUID,
        val username: String
    )
}
