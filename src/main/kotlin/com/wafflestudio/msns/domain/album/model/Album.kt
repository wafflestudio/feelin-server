package com.wafflestudio.msns.domain.album.model

import com.wafflestudio.msns.domain.artist.model.Artist
import com.wafflestudio.msns.domain.model.BaseEntity
import com.wafflestudio.msns.domain.track.model.Track
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "album")
class Album(
    @Column(unique = true)
    @field:NotBlank
    val title: String,

    @field:NotBlank
    val description: String,

    @field:NotNull
    val releaseDate: LocalDate,

    val cover: String = "",

    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    val artist: Artist,

    @OneToMany(mappedBy = "album", fetch = FetchType.LAZY)
    val tracks: MutableList<Track> = mutableListOf(),

) : BaseEntity()
