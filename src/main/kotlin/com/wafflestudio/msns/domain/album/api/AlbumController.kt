package com.wafflestudio.msns.domain.album.api

import com.wafflestudio.msns.domain.album.service.AlbumService
import org.springframework.web.bind.annotation.RestController

@RestController
class AlbumController(
    private val albumService: AlbumService,
)
