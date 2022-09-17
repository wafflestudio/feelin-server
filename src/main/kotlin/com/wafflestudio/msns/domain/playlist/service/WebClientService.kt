package com.wafflestudio.msns.domain.playlist.service

import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
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

    fun createUser(userId: UUID, username: String): Mono<UserResponse.PostAPIDto> =
        webClient
            .post()
            .uri("/users")
            .body(Mono.just(UserRequest.PostAPIDto(userId, username)), UserRequest.PostAPIDto::class.java)
            .retrieve()
            .bodyToMono(UserResponse.PostAPIDto::class.java)
}
