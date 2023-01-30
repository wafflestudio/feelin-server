package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.model.Block
import com.wafflestudio.msns.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BlockRepository : JpaRepository<Block, UUID?>, BlockCustomRepository {
    fun existsByFromUser_IdAndToUser(fromUserId: UUID, toUser: User): Boolean

    fun existsByFromUserAndToUser(fromUser: User, toUser: User): Boolean

    fun existsByFromUserAndToUser_Id(fromUser: User, toUserId: UUID): Boolean
}
