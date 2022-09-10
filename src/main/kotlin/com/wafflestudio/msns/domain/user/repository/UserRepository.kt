package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long?> {
    fun findByEmail(email: String): User?
    fun findByUsername(username: String): User?
    fun findByPhoneNumber(phoneNumber: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByPhoneNumber(phoneNumber: String): Boolean
}
