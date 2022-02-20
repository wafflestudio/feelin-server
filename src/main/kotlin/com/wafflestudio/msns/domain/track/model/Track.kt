package com.wafflestudio.msns.domain.track.model

import com.wafflestudio.msns.domain.model.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "track")
class Track(
    @Column(unique = true)
    val title: String,

    // TODO: Artist
) : BaseEntity()
