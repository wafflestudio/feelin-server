package com.wafflestudio.msns.domain.track.service

import com.wafflestudio.msns.domain.track.repository.TrackRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TrackService(
    private val trackRepository: TrackRepository,
)
