package com.wafflestudio.msns.domain.user.repository

import com.querydsl.core.types.ConstantImpl
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.msns.domain.post.model.QPost.post
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.QBlock.block
import com.wafflestudio.msns.domain.user.model.QFollow.follow
import com.wafflestudio.msns.domain.user.model.QLike.like
import com.wafflestudio.msns.domain.user.model.QUser.user
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.global.util.QueryDslUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import java.util.UUID

class LikeCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : LikeCustomRepository {
    override fun getLikes(
        loginUser: User,
        cursor: String?,
        pageable: Pageable,
        postId: UUID
    ): Slice<UserResponse.LikeListResponse> {
        val orders: List<OrderSpecifier<*>> = QueryDslUtil.getAllOrderSpecifier(pageable.sort, like)
        val fetch = queryFactory
            .select(
                Projections.constructor(
                    UserResponse.LikeListResponse::class.java,
                    user.id,
                    user.username,
                    user.profileImageUrl,
                    like.createdAt,
                    JPAExpressions
                        .selectFrom(follow)
                        .where(follow.toUser.eq(user).and(follow.fromUser.eq(loginUser)))
                        .exists()
                )
            )
            .from(like)
            .join(like.post, post)
            .join(like.user, user)
            .where(
                post.id.eq(postId)
                    .and(
                        JPAExpressions
                            .selectFrom(block)
                            .where(block.fromUser.eq(loginUser).and(block.toUser.eq(user)))
                            .exists()
                            .not()
                    )
                    .and(
                        JPAExpressions
                            .selectFrom(block)
                            .where(block.fromUser.eq(user).and(block.toUser.eq(loginUser)))
                            .exists()
                            .not()
                    )
                    .and(ltLikeCreatedAt(cursor))
            )
            .orderBy(*orders.toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong() + 1)

        val query = fetch.fetch()
            as MutableList<UserResponse.LikeListResponse>

        var hasNext = false
        if (query.size == pageable.pageSize + 1) {
            query.removeAt(pageable.pageSize)
            hasNext = true
        }

        return SliceImpl(query, pageable, hasNext)
    }

    private fun ltLikeCreatedAt(cursor: String?): BooleanExpression? {
        if (cursor == null) return null

        val createdAtStringTemplate = Expressions.stringTemplate(
            "DATE_FORMAT({0}, {1})",
            like.createdAt,
            ConstantImpl.create("%Y%m%d%H%i%S%f")
        )

        return createdAtStringTemplate.lt(cursor)
    }
}
