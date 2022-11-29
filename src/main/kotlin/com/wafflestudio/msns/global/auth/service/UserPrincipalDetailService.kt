package com.wafflestudio.msns.global.auth.service

import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.model.UserPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserPrincipalDetailService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(account: String): UserDetails {
        return userRepository.findSignInUser(account)
            ?.let{ UserPrincipal(it) }
            ?: throw UsernameNotFoundException("User with the account '${account}' not found.")
    }
}
