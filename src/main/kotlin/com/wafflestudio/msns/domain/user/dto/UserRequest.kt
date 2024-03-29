package com.wafflestudio.msns.domain.user.dto

import com.wafflestudio.msns.domain.post.dto.PostRequest
import com.wafflestudio.msns.global.enum.Report
import com.wafflestudio.msns.global.enum.Verify
import java.util.UUID
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class UserRequest {
    data class SignUp(
        @field:Email val email: String?,
        val phoneNumber: String?,
        val countryCode: String?,
        @field:NotBlank val password: String,
        @field:NotBlank val name: String,
        @field:NotBlank val username: String,
        @field:NotBlank val birthDate: String
    )

    data class PostAPIDto(
        @field:NotBlank val id: UUID,
        @field:NotBlank val username: String
    )

    data class PutProfile(
        @field:NotBlank val name: String,
        @field:NotBlank val username: String,
        val profileImageUrl: String?,
        @field:NotNull val introduction: String
    )

    data class ReportDto(
        @field:NotNull val reportType: Report,
        @field:NotNull val username: String,
        val post: PostRequest.ReportRequest?,
        @field:NotBlank val description: String
    )

    data class ActivityDto(
        @field:NotNull val username: String,
        val account: String?,
        val type: Verify?,
        val post: PostRequest.ActivityRequest?
    )

    data class SlackReportDto(
        val attachments: List<SlackReportMessageDto>
    )

    data class SlackReportMessageDto(
        val fallback: String,
        val color: String,
        val pretext: String,
        val author_name: String,
        val author_icon: String,
        val title: String,
        val text: String,
        val fields: List<SlackFieldDto>?,
        val footer: String
    )

    data class SlackFieldDto(
        val title: String,
        val value: String,
        val short: Boolean
    )

    data class BlockRequest(
        val id: UUID
    )

    data class SlackActivityDto(
        val attachments: List<SlackActivityMessageDto>
    )

    data class SlackActivityMessageDto(
        val fallback: String,
        val color: String,
        val pretext: String,
        val author_name: String,
        val author_icon: String,
        val title: String?,
        val text: String?,
        val fields: List<SlackFieldDto>?
    )
}
