package com.wafflestudio.msns.domain.user.api

import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.service.LikeService
import com.wafflestudio.msns.global.auth.CurrentUser
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/likes")
class LikeController(
    private val likeService: LikeService
) {
    @PostMapping("/posts/{post_id}")
    @ResponseStatus(HttpStatus.CREATED)
    fun writeLike(
        @CurrentUser user: User,
        @PathVariable("post_id") postId: UUID
    ) = likeService.writeLike(user, postId)

    @GetMapping("/posts/{post_id}")
    fun getLikes(
        @CurrentUser loginUser: User,
        @PathVariable("post_id") postId: UUID,
        @RequestParam("cursor", required = false) cursor: String?,
        @PageableDefault(
            size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC
        ) pageable: Pageable,
    ): ResponseEntity<Slice<UserResponse.LikeListResponse>> = likeService.getLikes(loginUser, cursor, pageable, postId)

    @DeleteMapping("/posts/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteLike(
        @CurrentUser user: User,
        @PathVariable("post_id") postId: UUID
    ) = likeService.deleteLike(user, postId)
}
