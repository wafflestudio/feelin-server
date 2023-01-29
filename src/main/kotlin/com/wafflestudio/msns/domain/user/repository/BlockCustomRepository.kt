package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface BlockCustomRepository {
    fun getBlocksByUser(
        loginUser: User,
        cursor: String?,
        pageable: Pageable
    ): Slice<UserResponse.BlockListResponse>

    fun deleteBlockByFromUserAndToUserId(
        fromUser: User,
        toUserId: UUID
    )
}
