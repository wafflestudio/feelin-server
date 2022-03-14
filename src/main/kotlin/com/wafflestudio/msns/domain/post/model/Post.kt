package com.wafflestudio.msns.domain.post.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.user.model.User
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "post")
class Post(
    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    val user: User,

) : BaseTimeEntity()
