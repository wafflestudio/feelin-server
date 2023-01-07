package com.wafflestudio.msns.domain.post.repository

import com.querydsl.core.types.ConstantImpl
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.core.types.dsl.StringExpressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.model.QPost.post
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.QLike.like
import com.wafflestudio.msns.domain.user.model.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl

class PostRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : PostRepositoryCustom {
    override fun getFeed(user: User, cursor: String?, pageable: Pageable): Slice<PostResponse.FeedResponse> {

        var fetch = queryFactory
            .select(
                Projections.constructor(
                    PostResponse.FeedResponse::class.java,
                    post.id,
                    post.title,
                    post.content,
                    Projections.constructor(
                        UserResponse.PreviewResponse::class.java,
                        post.user
                    ),
                    post.updatedAt,
                    Projections.constructor(
                        PlaylistResponse.PreviewResponse::class.java,
                        post.playlist
                    ),
                    post.likes.size(),
                    JPAExpressions
                        .selectFrom(like)
                        .where(like.post.eq(post).and(like.user.eq(user)))
                        .exists()
                )
            )
            .from(post)
            .where(ltPostUpdated(cursor))
            .limit(pageable.pageSize.toLong() + 1)

        pageable.sort.forEach {
            val pathBuilder = PathBuilder(post.type, post.metadata)
            fetch = fetch.orderBy(
                OrderSpecifier(
                    if (it.isAscending) Order.ASC else Order.DESC,
                    pathBuilder.getString(it.property)
                )
            )
        }

        val query = fetch.fetch()
            as MutableList<PostResponse.FeedResponse>

        var hasNext = false
        if (query.size == pageable.pageSize + 1) {
            query.removeAt(pageable.pageSize)
            hasNext = true
        }

        return SliceImpl(query, pageable, hasNext)
    }

    private fun ltPostUpdated(cursor: String?): BooleanExpression? {
        if (cursor == null) return null

        val stringTemplate = Expressions.stringTemplate(
            "DATE_FORMAT({0}, {1})",
            post.updatedAt,
            ConstantImpl.create("%Y%m%d%H%i%s")
        )

        return stringTemplate
            .concat(StringExpressions.lpad(post.id.stringValue(), 10, '0'))
            .lt(cursor)
    }
}
