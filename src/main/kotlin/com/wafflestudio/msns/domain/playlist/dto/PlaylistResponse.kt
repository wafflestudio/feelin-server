package com.wafflestudio.msns.domain.playlist.dto

import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.post.model.Post
import com.wafflestudio.msns.domain.track.dto.TrackResponse
import java.util.UUID

class PlaylistResponse {
    data class PreviewResponse(
        val id: UUID,
        val thumbnail: String
    ) {
        constructor(playlist: Playlist) : this(
            id = playlist.playlistId,
            thumbnail = playlist.thumbnail
        )
    }

    data class PreviewDto(
        val id: UUID,
        val thumbnail: String
    )

    data class APIResponse(
        val id: UUID,
        val title: String,
        var tracks: List<TrackResponse.APIDto>,
        val preview: PreviewDto
    )

    data class DetailResponse(
        val id: UUID,
        val thumbnail: String,
        val title: String,
        var tracks: List<TrackResponse.APIDto>
    ) {
        constructor(playlistResponse: APIResponse) : this(
            id = playlistResponse.id,
            thumbnail = playlistResponse.preview.thumbnail,
            title = playlistResponse.title,
            tracks = playlistResponse.tracks
        )
    }

    data class FeedPreviewResponse(
        val id: UUID,
        val thumbnail: String,
        val mainTracks: List<TrackResponse.FeedPreviewResponse>
    ) {
        constructor(post: Post) : this(
            id = post.playlist.playlistId,
            thumbnail = post.playlist.thumbnail,
            mainTracks = post.mainTracks.map { TrackResponse.FeedPreviewResponse(it) }
        )
    }
}
