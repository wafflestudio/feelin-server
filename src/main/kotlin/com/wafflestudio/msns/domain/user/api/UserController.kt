package com.wafflestudio.msns.domain.user.api

import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.service.UserService
import com.wafflestudio.msns.global.auth.CurrentUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
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
        @PathVariable("user_id") userId: Long
    ): UserResponse.ProfileResponse =
        userService.getProfileByUserId(loginUser, userId)

    @PutMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    fun putProfile(
        @CurrentUser user: User,
        @Valid @RequestBody putRequest: UserRequest.PutProfile
    ): UserResponse.MyProfileResponse = userService.putMyProfile(user, putRequest)

    @GetMapping("/{user_id}/posts")
    @ResponseStatus(HttpStatus.OK)
    fun getPosts(
        @PageableDefault(
            size = 30, sort = ["createdAt"], direction = Sort.Direction.DESC
        ) pageable: Pageable,
        @PathVariable("user_id") userId: Long
    ): Page<PostResponse.UserPageResponse> = userService.getMyPosts(pageable, userId)
}
