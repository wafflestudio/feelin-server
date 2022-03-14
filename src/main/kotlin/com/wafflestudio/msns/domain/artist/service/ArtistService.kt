package com.wafflestudio.msns.domain.artist.service

import com.wafflestudio.msns.domain.artist.repository.ArtistRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ArtistService(
    private val artistRepository: ArtistRepository,
)
