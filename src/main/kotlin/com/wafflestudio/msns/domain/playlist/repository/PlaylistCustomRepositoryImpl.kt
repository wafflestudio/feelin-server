package com.wafflestudio.msns.domain.playlist.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.msns.domain.playlist.model.QPlaylist.playlist
import java.util.UUID

class PlaylistCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): PlaylistCustomRepository {
    override fun deleteAllByUserId(userId: UUID) {
        queryFactory
            .delete(playlist)
            .where(playlist.user.id.eq(userId))
            .execute()
    }
}
