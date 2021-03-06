package com.wafflestudio.msns.global.auth.model

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class AuthenticationToken(
    private val principal: VerificationTokenPrincipal,
    private var accessToken: Any?,
    authorities: Collection<GrantedAuthority?>? = null,
) : AbstractAuthenticationToken(authorities) {
    init {
        if (authorities == null)
            super.setAuthenticated(false)
        else super.setAuthenticated(true)
    }

    override fun getCredentials(): Any? {
        return accessToken
    }

    override fun getPrincipal(): VerificationTokenPrincipal {
        return principal
    }

    override fun eraseCredentials() {
        super.eraseCredentials()
        accessToken = null
    }
}
