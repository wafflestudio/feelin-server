package com.wafflestudio.msns.domain.post.api

import com.wafflestudio.msns.domain.post.dto.PostRequest
import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.service.PostService
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.global.auth.CurrentUser
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postService: PostService
) {
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun writePost(
        @Valid @RequestBody createRequest: PostRequest.CreateRequest,
        @CurrentUser user: User
    ) = postService.writePost(createRequest, user)

    @GetMapping("/feed")
    fun getFeeds(
        @PageableDefault(
            size = 20, sort = ["updatedAt"], direction = Sort.Direction.DESC
        ) pageable: Pageable,
        @RequestParam("cursor", required = false) cursor: String?,
        @RequestParam("follow", required = false, defaultValue = false.toString()) viewFollowers: Boolean,
        @CurrentUser user: User
    ): ResponseEntity<Slice<PostResponse.FeedResponse>> = postService.getFeed(user, viewFollowers, cursor, pageable)

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun getPosts(
        @PageableDefault(
            size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC
        ) pageable: Pageable,
        @RequestParam("cursor", required = false) cursor: String?,
        @RequestParam("userId", required = false, defaultValue = "-1") userId: Long,
        @CurrentUser user: User
    ): ResponseEntity<Slice<PostResponse.PreviewResponse>> =
        postService.getPosts(if (userId < 0) user.id else userId, cursor, pageable)

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getPost(
        @PathVariable("postId") postId: Long,
        @CurrentUser user: User
    ): PostResponse.DetailResponse = postService.getPostById(user, postId)

    @GetMapping("/{postId}/playlist/order")
    @ResponseStatus(HttpStatus.OK)
    fun getPostPlaylistOrder(
        @PathVariable("postId") postId: Long
    ): PostResponse.PlaylistOrderResponse = postService.getPostPlaylistOrder(postId)

    @PutMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    fun modifyPost(
        @Valid @RequestBody putRequest: PostRequest.PutRequest,
        @PathVariable("postId") postId: Long,
        @CurrentUser user: User
    ) = postService.modifyPost(putRequest, postId, user)

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    fun deletePost(
        @PathVariable("postId") postId: Long,
        @CurrentUser user: User
    ) = postService.deletePost(postId, user)
}
