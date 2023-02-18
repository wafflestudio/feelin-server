package com.wafflestudio.msns.domain.playlist.dto

import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.post.model.Post
import com.wafflestudio.msns.domain.track.dto.TrackResponse
import java.util.UUID

class PlaylistResponse {
    data class PreviewResponse(
        val id: UUID,
        val thumbnail: String,
        val originalVendorPlaylist: OriginalVendorResponse
    ) {
        constructor(playlist: Playlist) : this(
            id = playlist.playlistId,
            thumbnail = playlist.thumbnail,
            originalVendorPlaylist = OriginalVendorResponse(playlist.url, playlist.vendor)
        )
    }

    data class OriginalVendorResponse(
        val url: String,
        val vendor: String
    )

    data class PreviewDto(
        val id: UUID,
        val thumbnail: String
    )

    data class VendorPlaylistDto(
        val vendor: String,
        val url: String
    )

    data class APIResponse(
        val id: UUID,
        val title: String,
        var tracks: List<TrackResponse.APIDto>,
        val originalVendorPlaylist: VendorPlaylistDto,
        val preview: PreviewDto
    )

    data class DetailResponse(
        val id: UUID,
        val thumbnail: String,
        val title: String,
        val originalVendorPlaylist: VendorPlaylistDto,
        var tracks: List<TrackResponse.APIDto>
    ) {
        constructor(playlistResponse: APIResponse) : this(
            id = playlistResponse.id,
            thumbnail = playlistResponse.preview.thumbnail,
            title = playlistResponse.title,
            originalVendorPlaylist = playlistResponse.originalVendorPlaylist,
            tracks = playlistResponse.tracks
        )
    }

    data class FeedPreviewResponse(
        val id: UUID,
        val thumbnail: String,
        val mainTracks: List<TrackResponse.FeedPreviewResponse>,
        val originalVendorPlaylist: OriginalVendorResponse
    ) {
        constructor(post: Post) : this(
            id = post.playlist.playlistId,
            thumbnail = post.playlist.thumbnail,
            mainTracks = post.mainTracks.map { TrackResponse.FeedPreviewResponse(it) },
            originalVendorPlaylist = OriginalVendorResponse(post.playlist.url, post.playlist.vendor)
        )
    }
}
