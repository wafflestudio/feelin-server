package com.wafflestudio.msns.domain.user.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.user.dto.UserResponse
import javax.persistence.ColumnResult
import javax.persistence.ConstructorResult
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.NamedNativeQuery
import javax.persistence.SqlResultSetMapping
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@NamedNativeQuery(
    name = "Follow.findAllWithFromUserId",
    query = "SELECT u.id AS id, u.username AS username, u.image AS image " +
        "FROM Follow f INNER JOIN user u ON f.to_user_id = u.id WHERE f.from_user_id = :fromUserId",
    resultSetMapping = "FollowingListResponse"
)
@SqlResultSetMapping(
    name = "FollowingListResponse",
    classes = [
        ConstructorResult(
            targetClass = UserResponse.FollowingListResponse::class,
            columns = arrayOf(
                ColumnResult(name = "id", type = Long::class),
                ColumnResult(name = "username", type = String::class),
                ColumnResult(name = "image", type = String::class)
            )
        )
    ]
)
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
