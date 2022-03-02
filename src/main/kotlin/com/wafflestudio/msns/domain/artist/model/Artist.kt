package com.wafflestudio.msns.domain.artist.model

import com.wafflestudio.msns.domain.album.model.Album
import com.wafflestudio.msns.domain.model.BaseEntity
import com.wafflestudio.msns.domain.user.model.User
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "artist")
class Artist(
    @Column(unique = true)
    @field:NotBlank
    val artistName: String,

    @OneToOne(mappedBy = "artist")
    val user: User,

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = [])
    val albums: MutableList<Album> = mutableListOf(),

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY, cascade = [])
    val featuringTrack: MutableList<FeaturingArtist> = mutableListOf(),

) : BaseEntity()
