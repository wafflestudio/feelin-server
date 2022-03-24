package com.wafflestudio.msns.domain.post.api

import com.wafflestudio.msns.domain.post.dto.PostRequest
import com.wafflestudio.msns.domain.post.service.PostService
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.global.auth.CurrentUser
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/post")
class PostController(
    private val postService: PostService,
) {
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun writePost(@Valid @RequestBody createRequest: PostRequest.CreateRequest, @CurrentUser user: User) {
        postService.writePost(createRequest, user)
    }

    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun modifyPost(
        @Valid @RequestBody putRequest: PostRequest.PutRequest,
        @RequestParam("playlist") playlistTitle: String,
        @CurrentUser user: User) {
        postService.modifyPost(putRequest, playlistTitle, user)
    }
}
