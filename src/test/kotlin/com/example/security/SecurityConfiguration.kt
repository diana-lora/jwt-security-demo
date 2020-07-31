package com.example.security

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@Import(TokenProvider::class)
class SecurityConfiguration(private val tokenProvider: TokenProvider): WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .headers().cacheControl().disable() // allow client side caching
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()

            // actuator endpoints are free
            .antMatchers(HttpMethod.GET, "/actuator/**").permitAll()
            // anything else should be authenticated
            .antMatchers("/users/**").authenticated()
            .and()
            .apply(JwtConfigurer(tokenProvider))
    }

}