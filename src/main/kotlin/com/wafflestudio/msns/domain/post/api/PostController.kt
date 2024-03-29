package com.wafflestudio.msns.domain.post.api

import com.wafflestudio.msns.domain.post.dto.PostRequest
import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.service.PostService
import com.wafflestudio.msns.domain.user.dto.UserRequest
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.service.ReportClientService
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
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postService: PostService,
    private val reportClientService: ReportClientService
) {
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun writePost(
        @Valid @RequestBody createRequest: PostRequest.CreateRequest,
        @CurrentUser user: User,
        @RequestParam(name = "alert", defaultValue = true.toString(), required = false) alert: Boolean,
    ): PostResponse.CreateResponse = postService.writePost(createRequest, user, alert)

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
    fun getPosts(
        @PageableDefault(
            size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC
        ) pageable: Pageable,
        @RequestParam("cursor", required = false) cursor: String?,
        @RequestParam("userId", required = false, defaultValue = "00000000-0000-0000-0000-000000000000") userId: UUID,
        @CurrentUser loginUser: User
    ): ResponseEntity<Slice<PostResponse.PreviewResponse>> =
        postService.getPosts(loginUser, if (userId == UUID(0, 0)) loginUser.id else userId, cursor, pageable)

    @GetMapping("/{postId}")
    suspend fun getPost(
        @PathVariable("postId") postId: UUID,
        @CurrentUser loginUser: User
    ): ResponseEntity<PostResponse.DetailResponse> = postService.getPostById(loginUser, postId)

    @GetMapping("/{postId}/playlist/order")
    fun getPostPlaylistOrder(
        @PathVariable("postId") postId: UUID,
        @CurrentUser loginUser: User
    ): ResponseEntity<PostResponse.PlaylistOrderResponse> = postService.getPostPlaylistOrder(loginUser, postId)

    @PutMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    fun modifyPost(
        @Valid @RequestBody putRequest: PostRequest.PutRequest,
        @PathVariable("postId") postId: UUID,
        @CurrentUser user: User
    ) = postService.modifyPost(putRequest, postId, user)

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    fun deletePost(
        @PathVariable("postId") postId: UUID,
        @CurrentUser user: User
    ) = postService.deletePost(postId, user)

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    fun reportUser(
        @CurrentUser user: User,
        @Valid @RequestBody reportRequest: UserRequest.ReportDto
    ): String? = reportClientService.noticeReport(user.username, reportRequest, isUser = false)
}
