package com.wafflestudio.msns.domain.post.service

import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import com.wafflestudio.msns.domain.playlist.exception.PlaylistNotFoundException
import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.playlist.repository.PlaylistRepository
import com.wafflestudio.msns.domain.playlist.service.WebClientService
import com.wafflestudio.msns.domain.post.dto.PostRequest
import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.exception.InvalidTitleException
import com.wafflestudio.msns.domain.post.exception.PostNotFoundException
import com.wafflestudio.msns.domain.post.model.Post
import com.wafflestudio.msns.domain.post.repository.PostRepository
import com.wafflestudio.msns.domain.user.model.User
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostService(
    private val postRepository: PostRepository,
    private val playlistRepository: PlaylistRepository,
    private val webClientService: WebClientService,
    private val modelMapper: ModelMapper
) {
    fun writePost(createRequest: PostRequest.CreateRequest, user: User) {
        val title = createRequest.title
            .also { if (it.isBlank()) throw InvalidTitleException("title is blank.") }
        val content = createRequest.content
        val playlist = playlistRepository.findByStreamId(createRequest.playlistPreview.id)
            ?: playlistRepository.save(
                Playlist(
                    user = user,
                    streamId = createRequest.playlistPreview.id,
                    thumbnail = createRequest.playlistPreview.thumbnail
                )
            )
        postRepository.save(
            Post(
                user = user,
                title = title,
                content = content,
                playlist = playlist
            )
        )
    }

    fun getPosts(pageable: Pageable, user: User): Page<PostResponse.PreviewResponse> =
        postRepository.findUserPosts(pageable, user).map { post -> PostResponse.PreviewResponse(post) }

    fun getPostById(postId: Long): PostResponse.DetailResponse =
        postRepository.findPostById(postId)
            ?.let { post ->
                webClientService.getPlaylist(post.playlist.streamId)
                    .let {
                        modelMapper.map(it.block(), PlaylistResponse.DetailResponse::class.java)
                            .let { playlist -> PostResponse.DetailResponse(post, playlist) }
                    }
            }
            ?: throw PostNotFoundException("post is not found with the id.")

    fun modifyPost(putRequest: PostRequest.PutRequest, playlistId: Long, user: User) {
        val title = putRequest.title
            .also { if (it.isBlank()) throw InvalidTitleException("title is blank.") }
        val content = putRequest.content

        postRepository.findByUser_IdAndPlaylist_Id(user.id, playlistId)
            ?.apply {
                this.title = title
                this.content = content
            }
            ?.let { postRepository.save(it) }
            ?: throw PlaylistNotFoundException("playlist is not found from the requested title.")
    }

    fun deletePost(playlistId: Long, user: User) {
        postRepository.findByUser_IdAndPlaylist_Id(user.id, playlistId)
            ?.let { postRepository.deleteById(it.id) }
            ?: throw PlaylistNotFoundException("playlist is not found from the requested title.")
    }
}
