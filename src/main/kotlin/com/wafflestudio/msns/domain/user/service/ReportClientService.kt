package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.playlist.exception.InternalServerException
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.global.enum.Verify
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import reactor.core.publisher.Mono

@Service
class ReportClientService(
    private val slackClient: WebClient
) {
    fun noticeReport(username: String, reportRequest: UserRequest.ReportDto, isUser: Boolean): String? {
        val slackReport = UserRequest.SlackReportDto(
            listOf(
                UserRequest.SlackReportMessageDto(
                    fallback = "⚠️ 신고 메시지 요청이 실패했습니다. ⚠️",
                    color = "#FF0000",
                    pretext = when (isUser) {
                        true -> "⚠️ 사용자 신고 ⚠️"
                        false -> "⚠️ 게시물 신고 ⚠️"
                    },
                    author_name = reportRequest.username,
                    author_icon = "https://avatars.githubusercontent.com/u/1299328?s=200&v=4",
                    title = "‼️신고 사유‼️",
                    text = reportRequest.reportType.description + " / " + reportRequest.description,
                    fields = reportRequest.post?.let {
                        listOf(
                            UserRequest.SlackFieldDto(
                                title = "📚신고 게시물📚",
                                value = "아이디: ${it.id}\n 제목: ${it.title}\n 내용: ${it.content}",
                                short = true
                            )
                        )
                    },
                    footer = "$username 님의 신고"
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

    suspend fun noticeNewActivity(newActivityRequest: UserRequest.ActivityDto, isUser: Boolean): String {
        val slackReport = UserRequest.SlackActivityDto(
            listOf(
                when (isUser) {
                    true -> UserRequest.SlackActivityMessageDto(
                        fallback = "⚠️ 새로운 활동 메시지 요청이 실패했습니다. ⚠️",
                        color = "#00FF00",
                        pretext = "🔆 새로운 사용자 🔆",
                        author_name = newActivityRequest.username,
                        author_icon = "https://avatars.githubusercontent.com/u/1299328?s=200&v=4",
                        title = "✒️ 가입 계정 ✒️",
                        text = when (newActivityRequest.type!!) {
                            Verify.EMAIL -> "✉️: ${newActivityRequest.account}"
                            Verify.PHONE -> "📞: ${newActivityRequest.account}"
                            Verify.NONE -> null
                        },
                        fields = null
                    )
                    false -> UserRequest.SlackActivityMessageDto(
                        fallback = "⚠️ 새로운 활동 메시지 요청이 실패했습니다. ⚠️",
                        color = "#00FF00",
                        pretext = "🎵 새로운 게시글 🎵",
                        author_name = newActivityRequest.username,
                        author_icon = "https://avatars.githubusercontent.com/u/1299328?s=200&v=4",
                        title = null,
                        text = null,
                        fields = newActivityRequest.post?.let {
                            listOf(
                                UserRequest.SlackFieldDto(
                                    title = "📚 게시물 정보 📚",
                                    value = "아이디: ${it.id}\n 제목: ${it.title}\n 내용: ${it.content}",
                                    short = true
                                )
                            )
                        }
                    )
                }
            )
        )

        return slackClient
            .post()
            .uri("/services/T06UKPBS8/B04N0MGC4UD/v1CIxzWXrgyFvD9f0cRMz4lg")
            .body(Mono.just(slackReport), UserRequest.SlackActivityDto::class.java)
            .awaitExchange { res ->
                if (res.statusCode().is2xxSuccessful) res.awaitBody()
                else throw InternalServerException("Internal Server Error.")
            }
    }
}
