package com.wafflestudio.msns.domain.playlist.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.user.model.User
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.Column

@Entity
@Table(
    name = "playlist",
    uniqueConstraints = [UniqueConstraint(columnNames = ["streamId"])]
)
class Playlist(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    @Column(columnDefinition = "BINARY(16)")
    val streamId: UUID,

    val thumbnail: String,

) : BaseTimeEntity()
