package com.wafflestudio.msns.domain.user.repository

import com.wafflestudio.msns.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID?> {
    fun findByEmail(email: String): User?

    fun findByUsername(username: String): User?

    fun existsByUsername(username: String): Boolean

    fun existsByPhoneNumberAndCountryCode(phoneNumber: String, countryCode: String): Boolean

    fun existsByEmail(email: String): Boolean

    @Query("SELECT u FROM User u WHERE u.email = :account OR u.username = :account OR u.phoneNumber = :account")
    fun findSignInUser(@Param("account") account: String): User?

    fun findByCountryCodeAndPhoneNumber(countryCode: String, phoneNumber: String): User?

    @Transactional
    fun deleteUserById(id: UUID)
}
