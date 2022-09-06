package com.wafflestudio.msns.domain.playlist.service

import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class WebClientService(
    private val webClient: WebClient
) {
    fun getPlaylist(playlistId: UUID): Mono<PlaylistResponse.DetailResponse> =
        webClient
            .get()
            .uri("/playlists/$playlistId")
            .retrieve() // HttpStatus.OK
            .bodyToMono(PlaylistResponse.DetailResponse::class.java)
}
