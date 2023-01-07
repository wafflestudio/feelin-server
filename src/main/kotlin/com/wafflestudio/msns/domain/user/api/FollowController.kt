package com.wafflestudio.msns.domain.user.api

import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.service.FollowService
import com.wafflestudio.msns.global.auth.CurrentUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/follows")
class FollowController(
    private val followService: FollowService
) {
    @PostMapping("/{user_id}")
    @ResponseStatus(HttpStatus.CREATED)
    fun makeFollow(
        @CurrentUser user: User,
        @PathVariable("user_id") userId: Long
    ) = followService.makeFollow(fromUser = user, toUserId = userId)

    @GetMapping("/followings/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    fun getFollowings(
        @PathVariable("user_id") userId: Long,
        @PageableDefault(size = 10) pageable: Pageable
    ): Page<UserResponse.FollowListResponse> = followService.getFollowings(pageable, userId)

    @GetMapping("/followers/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    fun getFollowers(
        @PathVariable("user_id") userId: Long,
        @PageableDefault(size = 10) pageable: Pageable
    ): Page<UserResponse.FollowListResponse> = followService.getFollowers(pageable, userId)

    @DeleteMapping("/{user_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFollow(
        @CurrentUser user: User,
        @PathVariable("user_id") userId: Long
    ) = followService.deleteFollow(user, userId)
}
