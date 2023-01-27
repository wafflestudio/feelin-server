package com.wafflestudio.msns.domain.playlist.repository

import com.wafflestudio.msns.domain.playlist.model.Playlist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface PlaylistRepository : JpaRepository<Playlist, UUID?> {
    fun findByPlaylistId(playlistId: UUID): Playlist?

    @Transactional
    @Modifying
    @Query("DELETE FROM Playlist playlist WHERE playlist.user_id = :id", nativeQuery = true)
    fun deleteMappingByUserId(@Param("id") id: String)
}
