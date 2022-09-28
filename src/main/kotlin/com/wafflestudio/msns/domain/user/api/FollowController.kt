package com.wafflestudio.msns.domain.user.api

import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.service.FollowService
import com.wafflestudio.msns.global.auth.CurrentUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
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
        @PageableDefault(size = 30) pageable: Pageable
    ): Page<UserResponse.FollowingListResponse> = followService.getFollowings(pageable, userId)
}
