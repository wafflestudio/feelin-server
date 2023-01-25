package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.user.dto.UserRequest
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class ReportClientService(
    private val slackClient: WebClient
) {
    fun noticeReport(username: String, reportRequest: UserRequest.ReportDto): String? {
        val slackReport = UserRequest.SlackReportDto(
            listOf(
                UserRequest.SlackReportMessageDto(
                    "⚠️ 신고 메시지 요청이 실패했습니다. ⚠️",
                    "#FF0000",
                    "⚠️ 사용자 신고 ⚠️",
                    reportRequest.username,
                    "https://avatars.githubusercontent.com/u/1299328?s=200&v=4",
                    "‼️신고 사유‼️",
                    reportRequest.reportType.description + " / " + reportRequest.description,
                    reportRequest.post?.let {
                        listOf(
                            UserRequest.SlackReportFieldDto(
                                title = "📚신고 게시물📚",
                                value = "제목: " + it.title + " / " + "내용: " + it.content,
                                short = true
                            )
                        )
                    },
                    "$username 님의 신고"
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
}
