package com.wafflestudio.msns.domain.post.service

import com.wafflestudio.msns.domain.artist.dto.ArtistResponse
import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import com.wafflestudio.msns.domain.playlist.exception.InvalidPlaylistOrderException
import com.wafflestudio.msns.domain.playlist.exception.PlaylistNotFoundException
import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.playlist.repository.PlaylistRepository
import com.wafflestudio.msns.domain.playlist.service.PlaylistClientService
import com.wafflestudio.msns.domain.post.dto.PostRequest
import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.exception.ForbiddenDeletePostException
import com.wafflestudio.msns.domain.post.exception.ForbiddenPutPostException
import com.wafflestudio.msns.domain.post.exception.InvalidTitleException
import com.wafflestudio.msns.domain.post.exception.PostNotFoundException
import com.wafflestudio.msns.domain.post.model.Post
import com.wafflestudio.msns.domain.post.model.PostMainTrack
import com.wafflestudio.msns.domain.post.repository.PostRepository
import com.wafflestudio.msns.domain.track.dto.TrackResponse
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.domain.user.repository.BlockRepository
import com.wafflestudio.msns.domain.user.repository.LikeRepository
import com.wafflestudio.msns.global.util.CursorUtil
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class PostService(
    private val postRepository: PostRepository,
    private val playlistRepository: PlaylistRepository,
    private val likeRepository: LikeRepository,
    private val blockRepository: BlockRepository,
    private val playlistClientService: PlaylistClientService,
    private val modelMapper: ModelMapper
) {
    suspend fun writePost(createRequest: PostRequest.CreateRequest, user: User): PostResponse.CreateResponse {
        val title = createRequest.title
            .also { if (it.isBlank()) throw InvalidTitleException("title is blank.") }
        val content = createRequest.content
        val playlistDto = createRequest.playlist
        val order: List<Int> = playlistDto.order.split(" ").map { it.toInt() }.sorted()
        val length: Int = playlistDto.length
        if (order != (1..length).toList())
            throw InvalidPlaylistOrderException("playlist order must be list of 1, ..., length")

        val playlistTracks: List<TrackResponse.APIDto> = playlistClientService.getPlaylist(playlistDto.id).tracks
        val playlistMainTracks: MutableList<PostMainTrack> =
            playlistTracks.subList(0, if (playlistTracks.size < 3) playlistTracks.size else 3)
                .map { PostMainTrack(it.title, getArtistNameString(it.artists), it.album.thumbnail) }
                as MutableList<PostMainTrack>

        val playlist = playlistRepository.findByPlaylistId(playlistDto.id)
            ?: playlistRepository.save(
                Playlist(
                    user = user,
                    playlistId = playlistDto.id,
                    playlistOrder = playlistDto.order,
                    thumbnail = playlistDto.thumbnail
                )
            )
        return postRepository.save(
            Post(
                user = user,
                title = title,
                content = content,
                playlist = playlist,
                mainTracks = playlistMainTracks
            )
        )
            .let { newPost ->
                PostResponse.CreateResponse(
                    newPost.id,
                    newPost.title,
                    newPost.content,
                    PostResponse.PlaylistPostResponse(playlist.thumbnail)
                )
            }
    }

    fun getFeed(
        user: User,
        viewFollowers: Boolean,
        cursor: String?,
        pageable: Pageable
    ): ResponseEntity<Slice<PostResponse.FeedResponse>> {
        val httpHeaders = HttpHeaders()
        val httpBody: Slice<PostResponse.FeedResponse> = postRepository.getFeed(user, viewFollowers, cursor, pageable)
        val lastElement: PostResponse.FeedResponse? = httpBody.content.lastOrNull()
        val nextCursor: String? = CursorUtil.generateCustomCursor(lastElement?.updatedAt, lastElement?.createdAt)
        httpHeaders.set("cursor", nextCursor)
        return ResponseEntity(httpBody, httpHeaders, HttpStatus.OK)
    }

    fun getPosts(
        loginUser: User,
        userId: UUID,
        cursor: String?,
        pageable: Pageable
    ): ResponseEntity<Slice<PostResponse.PreviewResponse>> {
        if (blockRepository.existsByFromUser_IdAndToUser(userId, loginUser) ||
            blockRepository.existsByFromUserAndToUser_Id(loginUser, userId)
        )
            return ResponseEntity(null, null, HttpStatus.FORBIDDEN)
        val httpHeaders = HttpHeaders()
        val httpBody: Slice<PostResponse.PreviewResponse> = postRepository.getAllByUserId(userId, cursor, pageable)
        val lastElement: PostResponse.PreviewResponse? = httpBody.content.lastOrNull()
        val nextCursor: String? = CursorUtil.generateCustomCursor(lastElement?.updatedAt, lastElement?.createdAt)
        httpHeaders.set("cursor", nextCursor)
        return ResponseEntity(httpBody, httpHeaders, HttpStatus.OK)
    }

    suspend fun getPostById(loginUser: User, postId: UUID): ResponseEntity<PostResponse.DetailResponse> =
        postRepository.findPostById(postId)
            ?.also { post ->
                if (blockRepository.existsByFromUserAndToUser(post.user, loginUser) ||
                    blockRepository.existsByFromUserAndToUser(loginUser, post.user)
                )
                    return ResponseEntity(null, null, HttpStatus.FORBIDDEN)
            }
            ?.let { post ->
                playlistClientService.getPlaylist(post.playlist.playlistId)
                    .let { webDto ->
                        modelMapper.map(webDto, PlaylistResponse.APIResponse::class.java)
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
                                    PlaylistResponse.DetailResponse(playlist),
                                    likeRepository.countByPost_Id(postId),
                                    likeRepository.existsByPost_IdAndUser_Id(postId, loginUser.id)
                                )
                            }
                            .let { httpBody -> ResponseEntity(httpBody, null, HttpStatus.OK) }
                    }
            }
            ?: throw PostNotFoundException("post is not found with the given id.")

    fun modifyPost(putRequest: PostRequest.PutRequest, postId: UUID, user: User) {
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

    fun deletePost(postId: UUID, user: User) {
        postRepository.findPostById(postId)
            ?.also { if (it.user.id != user.id) throw ForbiddenDeletePostException("user cannot delete other's post.") }
            ?.let { likeRepository.deleteAllByPost_Id(postId) }
            ?.let { postRepository.deleteById(postId) }
            ?: throw PostNotFoundException("post is not found with the id.")
    }

    fun getPostPlaylistOrder(loginUser: User, postId: UUID): ResponseEntity<PostResponse.PlaylistOrderResponse> =
        postRepository.findPostById(postId)
            ?.also { post ->
                if (blockRepository.existsByFromUserAndToUser(post.user, loginUser) ||
                    blockRepository.existsByFromUserAndToUser(loginUser, post.user)
                )
                    return ResponseEntity(null, null, HttpStatus.FORBIDDEN)
            }
            ?.let { PostResponse.PlaylistOrderResponse(it.playlist.playlistOrder) }
            ?.let { httpBody -> ResponseEntity(httpBody, null, HttpStatus.OK) }
            ?: throw PostNotFoundException("post is not found with the id.")

    private fun getArtistNameString(artists: List<ArtistResponse.APIDto>): String = artists.joinToString { it.name }
}
