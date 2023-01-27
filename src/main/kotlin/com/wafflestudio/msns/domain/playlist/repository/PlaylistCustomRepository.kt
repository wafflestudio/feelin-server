package com.wafflestudio.msns.domain.playlist.repository

import java.util.UUID

interface PlaylistCustomRepository {
    fun deleteAllByUserId(userId: UUID)
}
