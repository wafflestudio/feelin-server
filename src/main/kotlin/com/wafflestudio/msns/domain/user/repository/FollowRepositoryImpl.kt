package com.wafflestudio.msns.domain.user.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.msns.domain.user.dto.UserResponse
import com.wafflestudio.msns.domain.user.model.QFollow.follow
import com.wafflestudio.msns.domain.user.model.QUser.user
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.UUID

class FollowRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : FollowCustomRepository {
    override fun getFollowingsByFromUserId(
        pageable: Pageable,
        fromUserId: UUID
    ): Page<UserResponse.FollowListResponse> {

        val fetch = queryFactory
            .select(
                Projections.constructor(
                    UserResponse.FollowListResponse::class.java,
                    user.id,
                    user.username,
                    user.profileImageUrl
                )
            )
            .from(follow)
            .join(follow.toUser, user)
            .where(follow.fromUser.id.eq(fromUserId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageImpl(fetch, pageable, fetch.size.toLong())
    }

    override fun getFollowingsByToUserId(
        pageable: Pageable,
        toUserId: UUID
    ): Page<UserResponse.FollowListResponse> {

        val fetch = queryFactory
            .select(
                Projections.constructor(
                    UserResponse.FollowListResponse::class.java,
                    user.id,
                    user.username,
                    user.profileImageUrl
                )
            )
            .from(follow)
            .join(follow.fromUser, user)
            .where(follow.toUser.id.eq(toUserId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageImpl(fetch, pageable, fetch.size.toLong())
    }
}
