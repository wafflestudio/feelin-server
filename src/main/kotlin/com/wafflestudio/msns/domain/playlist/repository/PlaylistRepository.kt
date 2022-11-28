package com.wafflestudio.msns.domain.playlist.repository

import com.wafflestudio.msns.domain.playlist.model.Playlist
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlaylistRepository : JpaRepository<Playlist, Long?> {
    fun findByPlaylistId(playlistId: UUID): Playlist?
}
