package com.wafflestudio.msns.domain.track.repository

import org.springframework.data.jpa.repository.JpaRepository
import javax.sound.midi.Track

interface TrackRepository : JpaRepository<Track, Long?>
