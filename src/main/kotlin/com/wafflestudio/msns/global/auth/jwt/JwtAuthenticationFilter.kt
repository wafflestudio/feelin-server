package com.wafflestudio.msns.global.auth.jwt

import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(
    authenticationManager: AuthenticationManager?,
    private val jwtTokenProvider: JwtTokenProvider,
    private val verificationTokenRepository: VerificationTokenRepository
) : BasicAuthenticationFilter(authenticationManager) {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        request.getHeader(jwtTokenProvider.headerString)
            ?.let { token ->
                if (jwtTokenProvider.validateToken(token)) {
                    verificationTokenRepository.findByAccessToken(token)
                        ?.let {
                            if (it.verification) {
                                val authentication = jwtTokenProvider.getAuthenticationTokenFromJwt(token)
                                SecurityContextHolder.getContext().authentication = authentication
                            }
                        }
                }
            }
        chain.doFilter(request, response)
    }
}
