package com.wafflestudio.msns.global.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.msns.domain.user.repository.UserRepository
import com.wafflestudio.msns.global.auth.jwt.JwtAuthenticationEntryPoint
import com.wafflestudio.msns.global.auth.jwt.JwtAuthenticationFilter
import com.wafflestudio.msns.global.auth.jwt.JwtTokenProvider
import com.wafflestudio.msns.global.auth.repository.VerificationTokenRepository
import com.wafflestudio.msns.global.auth.service.AuthService
import com.wafflestudio.msns.global.auth.service.UserPrincipalDetailService
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.CorsUtils
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
class SecurityConfig(
    private val userRepository: UserRepository,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val authService: AuthService,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userPrincipalDetailService: UserPrincipalDetailService,
    private val objectMapper: ObjectMapper,
    private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(daoAuthenticationProvider())
    }

    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder)
        provider.setUserDetailsService(userPrincipalDetailService)
        return provider
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()

        corsConfiguration.allowCredentials = true
        corsConfiguration.addAllowedOrigin("http://localhost:3000")
        corsConfiguration.addAllowedOrigin("http://ec2-54-180-105-114.ap-northeast-2.compute.amazonaws.com")
        corsConfiguration.addAllowedHeader("*")
        corsConfiguration.addAllowedMethod("*")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)

        return source
    }

    override fun configure(http: HttpSecurity) {
        http
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
            .addFilter(JwtAuthenticationFilter(authenticationManager(), jwtTokenProvider, authService))
            .authorizeRequests()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .antMatchers(HttpMethod.GET, "/ping").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/auth/email").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/auth/phone").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/auth/email/verify-code/send").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/auth/phone/verify-code/send").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/auth/email/verify-code").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/auth/phone/verify-code").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/auth/signup").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/auth/signin").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/auth/username").permitAll()
            .anyRequest().authenticated()
            .and()
            .logout()
            .logoutUrl("/api/v1/auth/signout")
            .logoutSuccessHandler(HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
    }
}
