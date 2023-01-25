package com.wafflestudio.msns.domain.test.api

import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import com.wafflestudio.msns.domain.playlist.service.PlaylistClientService
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/v1/test")
class TestController(
    private val playlistClient: WebClient,
    private val slackClient: WebClient,
    private val playlistClientService: PlaylistClientService
) {
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun test(
        @PathVariable("id") id: UUID
    ): PlaylistResponse.APIResponse? =
        playlistClient
            .get()
            .uri("/playlists/$id")
            .retrieve() // HttpStatus.OK
            .bodyToMono(PlaylistResponse.APIResponse::class.java)
            .block()

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    fun testReport(): String? {
        val slackReport = UserRequest.SlackReportDto(
            listOf(
                UserRequest.SlackReportMessageDto(
                    "⚠️ 신고 메시지 요청이 실패했습니다. ⚠️",
                    "#FF0000",
                    "⚠️ 사용자 신고 ⚠️",
                    "userA",
                    "https://avatars.githubusercontent.com/u/1299328?s=200&v=4",
                    "FALSE_INFO",
                    "reportRequest.description",
                    null,
                    "user 님의 신고"
                )
            )
        )
        return slackClient
            .post()
            .uri("/services/T06UKPBS8/B04KVN21V9D/1spMI0KxPQanhsUvFSP4ujrz")
            .body(Mono.just(slackReport), UserRequest.SlackReportDto::class.java)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(): UserResponse.PostAPIDto? =
        playlistClientService.createUser(
            UUID.fromString("f127733b-c716-46b1-987a-5ef7b4a158eb"),
            "nasty-user"
        )
}
