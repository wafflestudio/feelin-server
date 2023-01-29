package com.wafflestudio.msns.domain.user.repository

import com.querydsl.core.types.ConstantImpl
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.QBlock.block
import com.wafflestudio.msns.domain.user.model.QFollow.follow
import com.wafflestudio.msns.domain.user.model.QUser.user
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.global.util.QueryDslUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

open class FollowCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : FollowCustomRepository {
    override fun getFollowsByFromUserId(
        loginUser: User,
        cursor: String?,
        pageable: Pageable,
        fromUserId: UUID
    ): Slice<UserResponse.FollowListResponse> {
        val orders: List<OrderSpecifier<*>> = QueryDslUtil.getAllOrderSpecifier(pageable.sort, follow)
        val fetch = queryFactory
            .select(
                Projections.constructor(
                    UserResponse.FollowListResponse::class.java,
                    user.id,
                    user.username,
                    user.profileImageUrl,
                    follow.createdAt,
                    JPAExpressions
                        .selectFrom(follow)
                        .where(follow.toUser.eq(user).and(follow.fromUser.eq(loginUser)))
                        .exists()
                )
            )
            .from(follow)
            .join(follow.toUser, user)
            .where(
                follow.fromUser.id.eq(fromUserId)
                    .and(
                        JPAExpressions
                            .selectFrom(block)
                            .where(block.fromUser.eq(loginUser).and(block.toUser.eq(follow.toUser)))
                            .exists()
                            .not()
                    )
                    .and(
                        JPAExpressions
                            .selectFrom(block)
                            .where(block.fromUser.eq(follow.toUser).and(block.fromUser.eq(loginUser)))
                            .exists()
                            .not()
                    )
                    .and(ltFollowCreatedAt(cursor))
            )
            .orderBy(*orders.toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong() + 1)

        val query = fetch.fetch()
            as MutableList<UserResponse.FollowListResponse>

        var hasNext = false
        if (query.size == pageable.pageSize + 1) {
            query.removeAt(pageable.pageSize)
            hasNext = true
        }

        return SliceImpl(query, pageable, hasNext)
    }

    override fun getFollowsByToUserId(
        loginUser: User,
        cursor: String?,
        pageable: Pageable,
        toUserId: UUID
    ): Slice<UserResponse.FollowListResponse> {
        val orders: List<OrderSpecifier<*>> = QueryDslUtil.getAllOrderSpecifier(pageable.sort, follow)
        val fetch = queryFactory
            .select(
                Projections.constructor(
                    UserResponse.FollowListResponse::class.java,
                    user.id,
                    user.username,
                    user.profileImageUrl,
                    follow.createdAt,
                    JPAExpressions
                        .selectFrom(follow)
                        .where(follow.toUser.eq(user).and(follow.fromUser.eq(loginUser)))
                        .exists()
                )
            )
            .from(follow)
            .join(follow.fromUser, user)
            .where(
                follow.toUser.id.eq(toUserId)
                    .and(
                        JPAExpressions
                            .selectFrom(block)
                            .where(block.fromUser.eq(loginUser).and(block.toUser.eq(follow.fromUser)))
                            .exists()
                            .not()
                    )
                    .and(
                        JPAExpressions
                            .selectFrom(block)
                            .where(block.fromUser.eq(follow.fromUser).and(block.toUser.eq(loginUser)))
                            .exists()
                            .not()
                    )
                    .and(ltFollowCreatedAt(cursor))
            )
            .orderBy(*orders.toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong() + 1)

        val query = fetch.fetch()
            as MutableList<UserResponse.FollowListResponse>

        var hasNext = false
        if (query.size == pageable.pageSize + 1) {
            query.removeAt(pageable.pageSize)
            hasNext = true
        }

        return SliceImpl(query, pageable, hasNext)
    }

    @Transactional
    override fun deleteFollowsByFromUserId(fromUserId: UUID) {
        queryFactory
            .delete(follow)
            .where(follow.fromUser.id.eq(fromUserId))
            .execute()
    }

    @Transactional
    override fun deleteFollowsByToUserId(toUserId: UUID) {
        queryFactory
            .delete(follow)
            .where(follow.toUser.id.eq(toUserId))
            .execute()
    }

    @Transactional
    override fun deleteFollowByFromUserAndToUserId(fromUser: User, toUserId: UUID) {
        queryFactory
            .delete(follow)
            .where(follow.fromUser.eq(fromUser).and(follow.toUser.id.eq(toUserId)))
            .execute()
    }

    @Transactional
    override fun deleteFollowByFromUserIdAndToUser(fromUserId: UUID, toUser: User) {
        queryFactory
            .delete(follow)
            .where(follow.fromUser.id.eq(fromUserId).and(follow.toUser.eq(toUser)))
            .execute()
    }

    private fun ltFollowCreatedAt(cursor: String?): BooleanExpression? {
        if (cursor == null) return null

        val createdAtStringTemplate = Expressions.stringTemplate(
            "DATE_FORMAT({0}, {1})",
            follow.createdAt,
            ConstantImpl.create("%Y%m%d%H%i%S%f")
        )

        return createdAtStringTemplate.lt(cursor)
    }
}
