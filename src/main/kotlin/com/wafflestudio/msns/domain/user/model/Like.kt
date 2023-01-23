package com.wafflestudio.msns.domain.user.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.post.model.Post
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(
    name = "likes",
    indexes = [
        Index(
            name = "unique_ui_pi",
            columnList = "user_id, post_id",
            unique = true
        )
    ]
)
class Like(
    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    val post: Post,

) : BaseTimeEntity()
