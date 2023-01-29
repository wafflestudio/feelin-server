package com.wafflestudio.msns.domain.user.repository

import com.querydsl.core.types.ConstantImpl
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.QBlock.block
import com.wafflestudio.msns.domain.user.model.QFollow
import com.wafflestudio.msns.domain.user.model.QUser.user
import com.wafflestudio.msns.domain.user.model.User
import com.wafflestudio.msns.global.util.QueryDslUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

open class BlockCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : BlockCustomRepository {
    override fun getBlocksByUser(
        loginUser: User,
        cursor: String?,
        pageable: Pageable
    ): Slice<UserResponse.BlockListResponse> {
        val orders: List<OrderSpecifier<*>> = QueryDslUtil.getAllOrderSpecifier(pageable.sort, block)
        val fetch = queryFactory
            .select(
                Projections.constructor(
                    UserResponse.BlockListResponse::class.java,
                    user.id,
                    user.username,
                    user.profileImageUrl,
                    QFollow.follow.createdAt,
                )
            )
            .from(block)
            .join(block.toUser, user)
            .where(block.fromUser.eq(loginUser).and(ltBlockCreatedAt(cursor)))
            .orderBy(*orders.toTypedArray())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong() + 1)

        val query = fetch.fetch()
            as MutableList<UserResponse.BlockListResponse>

        var hasNext = false
        if (query.size == pageable.pageSize + 1) {
            query.removeAt(pageable.pageSize)
            hasNext = true
        }

        return SliceImpl(query, pageable, hasNext)
    }

    @Transactional
    override fun deleteBlockByFromUserAndToUserId(fromUser: User, toUserId: UUID) {
        queryFactory
            .delete(block)
            .where(block.fromUser.eq(fromUser).and(block.toUser.id.eq(toUserId)))
            .execute()
    }

    private fun ltBlockCreatedAt(cursor: String?): BooleanExpression? {
        if (cursor == null) return null

        val createdAtStringTemplate = Expressions.stringTemplate(
            "DATE_FORMAT({0}, {1})",
            block.createdAt,
            ConstantImpl.create("%Y%m%d%H%i%S%f")
        )

        return createdAtStringTemplate.lt(cursor)
    }
}
