package com.wafflestudio.msns.domain.user.api

import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.service.BlockService
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
@RequestMapping("/api/v1/block")
class BlockController(
    private val blockService: BlockService
) {
    @PostMapping("/{user_id}")
    @ResponseStatus(HttpStatus.CREATED)
    fun blockUser(
        @CurrentUser user: User,
        @PathVariable("user_id") userId: UUID
    ) = blockService.blockUser(fromUser = user, toUserId = userId)

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun getBlocks(
        @RequestParam("cursor", required = false) cursor: String?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        @CurrentUser user: User
    ): ResponseEntity<Slice<UserResponse.BlockListResponse>> =
        blockService.getBlocks(user, cursor, pageable)

    @DeleteMapping("/{user_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unBlockUser(
        @CurrentUser user: User,
        @PathVariable("user_id") userId: UUID
    ) = blockService.unBlock(fromUser = user, toUserId = userId)
}
