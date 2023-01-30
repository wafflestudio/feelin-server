package com.wafflestudio.msns.domain.user.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(
    name = "block",
    indexes = [
        Index(
            name = "unique_idx_tui_fui",
            columnList = "to_user_id, from_user_id",
            unique = true
        )
    ]
)
class Block(
    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    @JoinColumn(name = "to_user_id", referencedColumnName = "id")
    val toUser: User,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    @JoinColumn(name = "from_user_id", referencedColumnName = "id")
    val fromUser: User,

) : BaseTimeEntity()
