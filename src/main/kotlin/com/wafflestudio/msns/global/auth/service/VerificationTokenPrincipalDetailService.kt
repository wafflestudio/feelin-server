package com.wafflestudio.msns.global.auth.service

import com.wafflestudio.msns.domain.user.exception.UserNotFoundException
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.model.VerificationTokenPrincipal
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class VerificationTokenPrincipalDetailService(
    private val userRepository: UserRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
) : UserDetailsService {
    override fun loadUserByUsername(account: String): UserDetails {
        val user = userRepository.findSignInUser(account)
            ?: throw UserNotFoundException("user is not found with the account information.")
        return verificationTokenRepository.findByEmail(user.email)
            ?.let { verificationToken -> VerificationTokenPrincipal(user, verificationToken) }
            ?: throw UsernameNotFoundException("User with email '${user.email}' not found.")
    }
}
