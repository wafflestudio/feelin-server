package com.wafflestudio.msns.domain.post.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.user.model.Like
import com.wafflestudio.msns.domain.user.model.User
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.ColumnResult
import javax.persistence.ConstructorResult
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.NamedNativeQueries
import javax.persistence.NamedNativeQuery
import javax.persistence.OneToMany
import javax.persistence.SqlResultSetMapping
import javax.persistence.SqlResultSetMappings

@Entity
@Table(name = "post")
@NamedNativeQueries(
    NamedNativeQuery(
        name = "Post.findAllWithMyId",
        query = "SELECT p.id AS id, p.title AS title, p.created_at AS created_at, " +
            "pl.thumbnail AS thumbnail " +
            "FROM post p " +
            "INNER JOIN playlist pl ON p.playlist_id = pl.id " +
            "WHERE p.user_id = :myId",
        resultSetMapping = "UserPageResponse"
    )
)
@SqlResultSetMappings(
    SqlResultSetMapping(
        name = "PreviewResponse",
        classes = [
            ConstructorResult(
                targetClass = PostResponse.PreviewResponse::class,
                columns = arrayOf(
                    ColumnResult(name = "post_id", type = Long::class),
                    ColumnResult(name = "title", type = String::class),
                    ColumnResult(name = "content", type = String::class),
                    ColumnResult(name = "created_at", type = LocalDateTime::class),
                    ColumnResult(name = "playlist_id", type = UUID::class),
                    ColumnResult(name = "thumbnail", type = String::class),
                    ColumnResult(name = "likes", type = Int::class)
                )
            )
        ]
    ),
    SqlResultSetMapping(
        name = "UserPageResponse",
        classes = [
            ConstructorResult(
                targetClass = PostResponse.UserPageResponse::class,
                columns = arrayOf(
                    ColumnResult(name = "id", type = Long::class),
                    ColumnResult(name = "title", type = String::class),
                    ColumnResult(name = "created_at", type = LocalDateTime::class),
                    ColumnResult(name = "thumbnail", type = String::class),
                )
            )
        ]
    )
)
class Post(
    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    var title: String,

    var content: String,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    @JoinColumn(name = "playlist_id", referencedColumnName = "id")
    val playlist: Playlist,

    @OneToMany(mappedBy = "post")
    val likes: MutableList<Like> = mutableListOf(),

) : BaseTimeEntity()
