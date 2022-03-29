package com.wafflestudio.msns.domain.album.dto

import com.wafflestudio.msns.domain.album.model.Album

class AlbumResponse {
    data class SimpleResponse(
        val id: Long,
        val title: String,
        val albumCover: String
    ) {
        constructor(album: Album) : this(
            id = album.id,
            title = album.title,
            albumCover = album.cover
        )
    }
}
