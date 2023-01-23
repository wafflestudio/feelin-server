package com.wafflestudio.msns.global.util

import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CursorUtil {
    companion object {
        fun generateCustomCursor(
            pageEndUpdatedAt: LocalDateTime?,
            pageEndCreatedAt: LocalDateTime?
        ): String? {
            if (pageEndUpdatedAt == null && pageEndCreatedAt == null) return null

            val customCursorEndUpdatedAt = getStringFromDate(pageEndUpdatedAt)
            val customCursorEndCreatedAt = getStringFromDate(pageEndCreatedAt)

            return customCursorEndUpdatedAt + customCursorEndCreatedAt // updatedAt(20) + createdAt(20)
        }

        fun generateCustomCursor(pageEndCreatedAt: LocalDateTime?): String? {
            if (pageEndCreatedAt == null) return null

            return getStringFromDate(pageEndCreatedAt) // createdAt(20)
        }

        private fun getStringFromDate(date: LocalDateTime?): String =
            date.toString()
                .replace("T".toRegex(), "")
                .replace("-".toRegex(), "")
                .replace(":".toRegex(), "")
                .replace("\\.".toRegex(), "")
    }
}
