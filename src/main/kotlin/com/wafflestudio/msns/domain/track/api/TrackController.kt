package com.wafflestudio.msns.domain.track.api

import com.wafflestudio.msns.domain.track.service.TrackService
import org.springframework.web.bind.annotation.RestController

@RestController
class TrackController(
    private val trackService: TrackService,
)
