package com.wafflestudio.msns.domain.playlist.repository

import com.wafflestudio.msns.domain.playlist.model.PlaylistTrack
import org.springframework.data.jpa.repository.JpaRepository

interface PlaylistTrackRepository : JpaRepository<PlaylistTrack, Long?>
