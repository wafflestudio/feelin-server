package com.wafflestudio.msns.domain.post.service

import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import com.wafflestudio.msns.domain.playlist.exception.InvalidPlaylistOrderException
import com.wafflestudio.msns.domain.playlist.exception.PlaylistNotFoundException
import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.playlist.repository.PlaylistRepository
import com.wafflestudio.msns.domain.playlist.service.WebClientService
import com.wafflestudio.msns.domain.post.dto.PostRequest
import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.exception.ForbiddenDeletePostException
import com.wafflestudio.msns.domain.post.exception.ForbiddenPutPostException
import com.wafflestudio.msns.domain.post.exception.InvalidTitleException
import com.wafflestudio.msns.domain.post.exception.PostNotFoundException
import com.wafflestudio.msns.domain.post.model.Post
import com.wafflestudio.msns.domain.post.repository.PostRepository
import com.wafflestudio.msns.domain.track.dto.TrackResponse
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.FollowRepository
import com.wafflestudio.msns.domain.user.repository.LikeRepository
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class PostService(
    private val postRepository: PostRepository,
    private val playlistRepository: PlaylistRepository,
    private val likeRepository: LikeRepository,
    private val webClientService: WebClientService,
    private val modelMapper: ModelMapper,
    private val followRepository: FollowRepository
) {
    fun writePost(createRequest: PostRequest.CreateRequest, user: User) {
        val title = createRequest.title
            .also { if (it.isBlank()) throw InvalidTitleException("title is blank.") }
        val content = createRequest.content
        val order: List<Int> = createRequest.playlist.order.split(" ").map { it.toInt() }.sorted()
        val length: Int = createRequest.playlist.length
        if (order != 1.rangeTo(length).toList())
            throw InvalidPlaylistOrderException("playlist order must be list of 1, ..., length")
        val playlist = playlistRepository.findByPlaylistId(createRequest.playlist.id)
            ?: playlistRepository.save(
                Playlist(
                    user = user,
                    playlistId = createRequest.playlist.id,
                    playlistOrder = createRequest.playlist.order,
                    thumbnail = createRequest.playlist.thumbnail
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

    fun getFeed(user: User, viewFollowers: Boolean, cursor: String?, pageable: Pageable): ResponseEntity<Slice<PostResponse.FeedResponse>> {
        val httpHeaders = HttpHeaders()
        val httpBody: Slice<PostResponse.FeedResponse> = postRepository.getFeed(user, viewFollowers, cursor, pageable)
        val lastElement: PostResponse.FeedResponse? = httpBody.content.lastOrNull()
        val nextCursor: String? = generateCustomCursor(lastElement?.updatedAt, lastElement?.id)
        httpHeaders.set("cursor", nextCursor)
        return ResponseEntity(httpBody, httpHeaders, HttpStatus.OK)
    }

    private fun generateCustomCursor(cursorEndDate: LocalDateTime?, cursorId: Long?): String? {
        if (cursorEndDate == null && cursorId == null) return null

        val customCursorEndDate = cursorEndDate.toString()
            .replace("T".toRegex(), "")
            .replace("-".toRegex(), "")
            .replace(":".toRegex(), "")
            .substring(0 until 14)
        val customCursorId = String.format("%1$" + 10 + "s", cursorId).replace(' ', '0')
        return customCursorEndDate + customCursorId
    }

    fun getPosts(pageable: Pageable, userId: Long): Page<PostResponse.PreviewResponse> =
        postRepository.findAllWithUserId(pageable, userId)

    suspend fun getPostById(user: User, postId: Long): PostResponse.DetailResponse =
        postRepository.findPostById(postId)
            ?.let { post ->
                webClientService.getPlaylist(post.playlist.playlistId)
                    .let { webDto ->
                        modelMapper.map(webDto, PlaylistResponse.DetailResponse::class.java)
                            .also { playlist ->
                                val playlistOrder: List<Int> =
                                    post.playlist.playlistOrder.split(" ").map { track -> track.toInt() }
                                val playlistOrderPair: List<Pair<Int, TrackResponse.APIDto>> =
                                    playlistOrder.zip(playlist.tracks).sortedWith(compareBy { it.first })
                                playlist.tracks = playlistOrderPair.map { it.second }
                            }
                            .let { playlist ->
                                PostResponse.DetailResponse(
                                    postId,
                                    post.title,
                                    post.content,
                                    UserResponse.PreviewResponse(post.user),
                                    post.createdAt,
                                    post.updatedAt,
                                    playlist,
                                    likeRepository.countByPost_Id(postId),
                                    likeRepository.existsByPost_IdAndUser_Id(postId, user.id)
                                )
                            }
                    }
            }
            ?: throw PostNotFoundException("post is not found with the given id.")

    fun modifyPost(putRequest: PostRequest.PutRequest, postId: Long, user: User) {
        val title = putRequest.title
            .also { if (it.isBlank()) throw InvalidTitleException("title is blank.") }
        val content = putRequest.content

        postRepository.findPostById(postId)
            ?.also { if (it.user.id != user.id) throw ForbiddenPutPostException("user cannot modify other's post.") }
            ?.also {
                val length: Int = it.playlist.playlistOrder.split(" ").size
                val order: List<Int> = putRequest.playlist.order.split(" ").map { it.toInt() }.sorted()
                if (order != 1.rangeTo(length).toList())
                    throw InvalidPlaylistOrderException("playlist order must be list of 1, ..., length.")
            }
            ?.apply {
                this.title = title
                this.content = content
            }
            ?.let { postRepository.save(it) }
            ?.playlist?.apply { this.playlistOrder = putRequest.playlist.order }
            ?.let { playlistRepository.save(it) }
            ?: throw PlaylistNotFoundException("playlist is not found with the given id.")
    }

    fun deletePost(postId: Long, user: User) {
        postRepository.findPostById(postId)
            ?.also { if (it.user.id != user.id) throw ForbiddenDeletePostException("user cannot delete other's post.") }
            ?.let { likeRepository.deleteAllByPost_Id(postId) }
            ?.let { postRepository.deleteById(postId) }
            ?: throw PostNotFoundException("post is not found with the id.")
    }

    fun getPostPlaylistOrder(postId: Long): PostResponse.PlaylistOrderResponse =
        postRepository.findPostById(postId)
            ?.let { PostResponse.PlaylistOrderResponse(it.playlist.playlistOrder) }
            ?: throw PostNotFoundException("post is not found with the id.")
}
