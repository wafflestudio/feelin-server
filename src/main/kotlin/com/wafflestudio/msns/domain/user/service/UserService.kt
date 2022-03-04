package com.wafflestudio.msns.domain.user.service

import com.wafflestudio.msns.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
)
