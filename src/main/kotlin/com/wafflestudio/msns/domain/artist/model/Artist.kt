package com.wafflestudio.msns.domain.artist.model

import com.wafflestudio.msns.domain.album.model.Album
import com.wafflestudio.msns.domain.model.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Index
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table(
    name = "artist",
    indexes = [
        Index(
            name = "unique_idx_an",
            columnList = "artist_name",
            unique = true
        )
    ]
)
class Artist(
    @Column(name = "artist_name", unique = true)
    @field:NotBlank
    val artistName: String,

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = [])
    val albums: MutableList<Album> = mutableListOf(),

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = [])
    val featuringTrack: MutableList<FeaturingArtist> = mutableListOf(),

) : BaseEntity()
