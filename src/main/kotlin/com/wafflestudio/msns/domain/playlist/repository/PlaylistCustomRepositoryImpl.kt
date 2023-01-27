package com.wafflestudio.msns.domain.playlist.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.msns.domain.playlist.model.QPlaylist.playlist
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

open class PlaylistCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : PlaylistCustomRepository {
    @Transactional
    override fun deleteAllByUserId(userId: UUID) {
        queryFactory
            .delete(playlist)
            .where(playlist.user.id.eq(userId))
            .execute()
    }
}
