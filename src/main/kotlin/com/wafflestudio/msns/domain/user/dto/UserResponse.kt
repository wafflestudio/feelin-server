package com.wafflestudio.msns.domain.user.dto

import com.wafflestudio.msns.domain.user.model.User
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

    data class PostAPIDto(
        val id: UUID,
        val username: String
    )
}
