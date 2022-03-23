package com.wafflestudio.msns.global.auth.service

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
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
        val verificationToken = verificationTokenRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User with email '$email' not found.")
        return VerificationTokenPrincipal(user, verificationToken)
    }
}
