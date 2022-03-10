package com.wafflestudio.msns.domain.track.repository

import com.wafflestudio.msns.domain.track.model.Track
import org.springframework.data.jpa.repository.JpaRepository

interface TrackRepository : JpaRepository<Track, Long?>
