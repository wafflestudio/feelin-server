package com.wafflestudio.msns.domain.playlist.service

import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import com.wafflestudio.msns.domain.playlist.exception.InternalServerException
import com.wafflestudio.msns.domain.playlist.exception.PlaylistNotFoundException
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class WebClientService(
    private val webClient: WebClient
) {
    suspend fun getPlaylist(playlistId: UUID): PlaylistResponse.APIResponse =
        webClient
            .get()
            .uri("/playlists/$playlistId")
            .awaitExchange { res ->
                if (res.statusCode().is2xxSuccessful) res.awaitBody()
                else if (res.statusCode().is4xxClientError) throw PlaylistNotFoundException("playlist is not found.")
                else throw InternalServerException("Internal Server Error.")
            }

    fun createUser(userId: UUID, username: String): Mono<UserResponse.PostAPIDto> =
        webClient
            .post()
            .uri("/users")
            .body(Mono.just(UserRequest.PostAPIDto(userId, username)), UserRequest.PostAPIDto::class.java)
            .retrieve()
            .bodyToMono(UserResponse.PostAPIDto::class.java)
}
