package com.wafflestudio.msns.global.util

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.PathBuilder
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class QueryDslUtil {
    companion object {
        fun getAllOrderSpecifier(sort: Sort, path: Path<*>): List<OrderSpecifier<*>> {
            val pathBuilder = PathBuilder(path.type, path.metadata)
            return sort.map {
                OrderSpecifier(
                    if (it.isAscending) Order.ASC else Order.DESC,
                    pathBuilder.getString(it.property)
                )
            }.toList()
        }
    }
}
