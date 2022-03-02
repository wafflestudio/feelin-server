package com.wafflestudio.msns.domain.artist.model

import com.wafflestudio.msns.domain.model.BaseEntity
import com.wafflestudio.msns.domain.track.model.Track
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "featuring_artist")
class FeaturingArtist(
    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    val track: Track,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    val artist: Artist,

) : BaseEntity()
