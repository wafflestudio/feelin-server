package com.wafflestudio.msns.domain.playlist.repository

import com.wafflestudio.msns.domain.playlist.model.Playlist
import org.springframework.data.jpa.repository.JpaRepository

interface PlaylistRepository : JpaRepository<Playlist, Long?> {
    fun findByUser_IdAndTitle(user_id: Long, title: String): Playlist?
}
