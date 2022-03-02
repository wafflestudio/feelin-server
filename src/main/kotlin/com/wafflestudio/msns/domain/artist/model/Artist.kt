package com.wafflestudio.msns.domain.artist.model

import com.wafflestudio.msns.domain.album.model.Album
import com.wafflestudio.msns.domain.model.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "artist")
class Artist(
    @Column(unique = true)
    @field:NotBlank
    val artistName: String,

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = [])
    val albums: MutableList<Album> = mutableListOf(),

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = [])
    val featuringTrack: MutableList<FeaturingArtist> = mutableListOf(),

) : BaseEntity()
