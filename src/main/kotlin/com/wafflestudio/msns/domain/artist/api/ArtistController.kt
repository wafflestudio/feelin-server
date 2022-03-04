package com.wafflestudio.msns.domain.artist.api

import com.wafflestudio.msns.domain.artist.service.ArtistService
import org.springframework.web.bind.annotation.RestController

@RestController
class ArtistController(
    private val artistService: ArtistService,
)
