package com.wafflestudio.msns.domain.test.api

import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID

@RestController
class TestController(
    private val webClient: WebClient
) {
    @GetMapping("/test/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun test(
        @PathVariable("id") id: UUID
    ): PlaylistResponse.APIResponse? =
        webClient
            .get()
            .uri("/playlists/$id")
            .retrieve() // HttpStatus.OK
            .bodyToMono(PlaylistResponse.APIResponse::class.java)
            .block()
}
