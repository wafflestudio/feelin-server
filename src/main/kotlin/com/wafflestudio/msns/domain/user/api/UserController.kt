package com.wafflestudio.msns.domain.user.api

import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.service.ReportClientService
import com.wafflestudio.msns.domain.user.service.UserService
import com.wafflestudio.msns.global.auth.CurrentUser
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
    private val reportClientService: ReportClientService
) {
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    fun getMyself(@CurrentUser user: User): UserResponse.DetailResponse = userService.getMyself(user)

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    fun getMyProfile(@CurrentUser user: User): UserResponse.MyProfileResponse = userService.getMyProfile(user)

    @GetMapping("/{user_id}/profile")
    @ResponseStatus(HttpStatus.OK)
    fun getUserProfile(
        @CurrentUser loginUser: User,
        @PathVariable("user_id") userId: UUID
    ): UserResponse.ProfileResponse =
        userService.getProfileByUserId(loginUser, userId)

    @PutMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    fun putProfile(
        @CurrentUser user: User,
        @Valid @RequestBody putRequest: UserRequest.PutProfile
    ): UserResponse.MyProfileResponse = userService.putMyProfile(user, putRequest)

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    fun reportUser(
        @CurrentUser user: User,
        @Valid @RequestBody reportRequest: UserRequest.ReportDto
    ): String? = reportClientService.noticeReport(user.username, reportRequest, isUser = true)
}
