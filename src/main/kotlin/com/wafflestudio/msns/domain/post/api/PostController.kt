package com.wafflestudio.msns.domain.post.api

import com.wafflestudio.msns.domain.post.dto.PostRequest
import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.service.PostService
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.global.auth.CurrentUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
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

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun getPosts(
        @PageableDefault(
            size = 30, sort = ["createdAt"], direction = Sort.Direction.DESC
        ) pageable: Pageable,
        @RequestParam("userId", required = false, defaultValue = "-1") userId: Long,
        @CurrentUser user: User
    ): Page<PostResponse.PreviewResponse> = postService.getPosts(pageable, if (userId < 0) user.id else userId)

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    fun getPost(
        @PathVariable("postId") postId: Long
    ): PostResponse.DetailResponse = postService.getPostById(postId)

    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun modifyPost(
        @Valid @RequestBody putRequest: PostRequest.PutRequest,
        @RequestParam("playlist_id") playlistId: Long,
        @CurrentUser user: User
    ) = postService.modifyPost(putRequest, playlistId, user)

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun deletePost(
        @RequestParam("playlist_id") playlistId: Long,
        @CurrentUser user: User
    ) = postService.deletePost(playlistId, user)
}
