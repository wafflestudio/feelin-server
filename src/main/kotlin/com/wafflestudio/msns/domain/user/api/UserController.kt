package com.wafflestudio.msns.domain.user.api

import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/{user_id}/posts")
    @ResponseStatus(HttpStatus.OK)
    fun getPosts(
        @PageableDefault(
            size = 30, sort = ["createdAt"], direction = Sort.Direction.DESC
        ) pageable: Pageable,
        @PathVariable("user_id") userId: Long
    ): Page<PostResponse.UserPageResponse> {
        return userService.getPosts(pageable, userId)
    }
}
