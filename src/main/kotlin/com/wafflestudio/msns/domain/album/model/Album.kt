package com.wafflestudio.msns.domain.album.model

import com.wafflestudio.msns.domain.model.BaseEntity
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "album")
class Album(
    @Column(unique = true)
    @field:NotBlank
    val title: String,

    @field:NotBlank
    val description: String,

    @field:NotBlank
    val releaseDate: LocalDate,
) : BaseEntity()
