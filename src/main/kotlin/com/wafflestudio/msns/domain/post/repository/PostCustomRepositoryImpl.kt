package com.wafflestudio.msns.domain.post.repository

import com.querydsl.core.types.ConstantImpl
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.msns.domain.playlist.dto.PlaylistResponse
import com.wafflestudio.msns.domain.post.dto.PostResponse
import com.wafflestudio.msns.domain.post.model.QPost.post
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.QFollow.follow
import com.wafflestudio.msns.domain.user.model.QLike.like
import com.wafflestudio.msns.domain.user.model.QUser.user
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.global.util.QueryDslUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import java.util.UUID

class PostCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : PostCustomRepository {
    override fun getFeed(
        loginUser: User,
        viewFollowers: Boolean,
        cursor: String?,
        pageable: Pageable
    ): Slice<PostResponse.FeedResponse> {
        val orders: List<OrderSpecifier<*>> = QueryDslUtil.getAllOrderSpecifier(pageable.sort, post)
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
                    post.createdAt,
                    Projections.constructor(
                        PlaylistResponse.PreviewResponse::class.java,
                        post.playlist
                    ),
                    post.likes.size(),
                    JPAExpressions
                        .selectFrom(like)
                        .where(like.post.eq(post).and(like.user.eq(loginUser)))
                        .exists()
                )
            )
            .from(post)
            .orderBy(*orders.toTypedArray())
            .limit(pageable.pageSize.toLong() + 1)

        fetch = if (viewFollowers)
            fetch
                .join(follow)
                .on(post.user.eq(follow.toUser))
                .where(
                    follow.fromUser.eq(loginUser).and(
                        ltPostUpdatedAt(cursor)
                    )
                )
        else fetch.where(ltPostUpdatedAt(cursor))

        val query = fetch.fetch()
            as MutableList<PostResponse.FeedResponse>

        var hasNext = false
        if (query.size == pageable.pageSize + 1) {
            query.removeAt(pageable.pageSize)
            hasNext = true
        }

        return SliceImpl(query, pageable, hasNext)
    }

    private fun ltPostUpdatedAt(cursor: String?): BooleanExpression? {
        if (cursor == null) return null

        val updatedAtStringTemplate = Expressions.stringTemplate(
            "DATE_FORMAT({0}, {1})",
            post.updatedAt,
            ConstantImpl.create("%Y%m%d%H%i%S%f")
        )
        val createdAtStringTemplate = Expressions.stringTemplate(
            "DATE_FORMAT({0}, {1})",
            post.createdAt,
            ConstantImpl.create("%Y%m%d%H%i%S%f")
        )

        return updatedAtStringTemplate.concat(createdAtStringTemplate).lt(cursor)
    }

    override fun getAllByUserId(
        userId: UUID,
        cursor: String?,
        pageable: Pageable
    ): Slice<PostResponse.PreviewResponse> {
        val orders: List<OrderSpecifier<*>> = QueryDslUtil.getAllOrderSpecifier(pageable.sort, post)
        val fetch = queryFactory
            .select(
                Projections.constructor(
                    PostResponse.PreviewResponse::class.java,
                    post.id,
                    post.title,
                    post.content,
                    post.createdAt,
                    post.updatedAt,
                    Projections.constructor(
                        PlaylistResponse.PreviewResponse::class.java,
                        post.playlist
                    ),
                    post.likes.size()
                )
            )
            .from(post)
            .join(post.user, user)
            .where(user.id.eq(userId).and(ltPostUpdatedAt(cursor)))
            .orderBy(*orders.toTypedArray())
            .limit(pageable.pageSize.toLong() + 1)

        val query = fetch.fetch()
            as MutableList<PostResponse.PreviewResponse>

        var hasNext = false
        if (query.size == pageable.pageSize + 1) {
            query.removeAt(pageable.pageSize)
            hasNext = true
        }

        return SliceImpl(query, pageable, hasNext)
    }

    override fun deleteAllByUserId(userId: UUID) {
        queryFactory
            .delete(post)
            .where(post.user.id.eq(userId))
            .execute()
    }
}
