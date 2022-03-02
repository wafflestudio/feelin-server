package com.wafflestudio.msns.domain.track.model

import com.wafflestudio.msns.domain.album.model.Album
import com.wafflestudio.msns.domain.artist.model.FeaturingArtist
import com.wafflestudio.msns.domain.model.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "track")
class Track(
    @Column(unique = true)
    val title: String,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    val album: Album,

    // TODO: Featuring artist = ManyToMany
    @OneToMany(mappedBy = "track", fetch = FetchType.LAZY)
    val featuringArtist: MutableList<FeaturingArtist> = mutableListOf(),

) : BaseEntity()
