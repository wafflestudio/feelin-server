package com.wafflestudio.msns.domain.album.service

import com.wafflestudio.msns.domain.album.repository.AlbumRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AlbumService(
    private val albumRepository: AlbumRepository,
)
