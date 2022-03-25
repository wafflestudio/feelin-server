package com.wafflestudio.msns.domain.post.api

import com.wafflestudio.msns.domain.post.dto.PostRequest
import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.service.PostService
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.global.auth.CurrentUser
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/post")
class PostController(
    private val postService: PostService,
) {
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun writePost(
        @Valid @RequestBody createRequest: PostRequest.CreateRequest,
        @CurrentUser user: User
    ) {
        postService.writePost(createRequest, user)
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun getPost(
        @RequestParam("playlist") playlistTitle: String,
        @RequestParam("email") email: String
    ): PostResponse.DetailResponse {
        return postService.getPost(playlistTitle, email)
    }

    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun modifyPost(
        @Valid @RequestBody putRequest: PostRequest.PutRequest,
        @RequestParam("playlist") playlistTitle: String,
        @CurrentUser user: User
    ) {
        postService.modifyPost(putRequest, playlistTitle, user)
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun deletePost(
        @RequestParam("playlist") playlistTitle: String,
        @CurrentUser user: User
    ) {
        postService.deletePost(playlistTitle, user)
    }
}
