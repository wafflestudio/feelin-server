package com.wafflestudio.msns.domain.post.model

import com.wafflestudio.msns.domain.model.BaseTimeEntity
import com.wafflestudio.msns.domain.playlist.model.Playlist
import com.wafflestudio.msns.domain.user.model.Like
import com.wafflestudio.msns.domain.user.model.User
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OrderColumn
import javax.validation.constraints.Size

@Entity
@Table(name = "post")
class Post(
    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    var title: String,

    @Size(max = 1000)
    @Column(name = "content", length = 1000)
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [])
    @JoinColumn(name = "playlist_id", referencedColumnName = "id")
    val playlist: Playlist,

    @OneToMany(mappedBy = "post")
    val likes: MutableList<Like> = mutableListOf(),

    @ElementCollection
    @CollectionTable(
        name = "post_main_track",
        joinColumns = [JoinColumn(name = "post_id", referencedColumnName = "id")]
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OrderColumn(name = "post_main_track_order")
    @JoinColumn(name = "post_id")
    val mainTracks: MutableList<PostMainTrack> = mutableListOf()

) : BaseTimeEntity()

@Embeddable
class PostMainTrack(
    val title: String,
    val artist: String,
    val thumbnail: String
)
