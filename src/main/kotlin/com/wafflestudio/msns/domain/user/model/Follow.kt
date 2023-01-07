package com.wafflestudio.msns.domain.user.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(name = "follow", uniqueConstraints = [UniqueConstraint(columnNames = ["to_user_id", "from_user_id"])])
class Follow(
    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    @JoinColumn(name = "to_user_id", referencedColumnName = "id")
    val toUser: User,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    @JoinColumn(name = "from_user_id", referencedColumnName = "id")
    val fromUser: User,

) : BaseTimeEntity()
