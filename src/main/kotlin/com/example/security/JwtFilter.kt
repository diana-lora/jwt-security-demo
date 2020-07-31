package com.example.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtFilter(private val tokenProvider: TokenProvider): OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtFilter::class.java)

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val jwt = resolveToken(request)
        try {
            // handle only in case a token was found on the request
            // if there is no token, spring security will care about the further handling
            if (StringUtils.hasText(jwt)) {
                tokenProvider.validateToken(jwt!!)
                val auth = tokenProvider.getAuthentication(jwt)
                SecurityContextHolder.getContext().authentication = auth
            }
            filterChain.doFilter(request, response)
        } catch (e: MalformedJwtException) {
            log.warn("Invalid JWT token! ", e)
            setResponse(response, "your token is malformed")
        } catch (e: SignatureException) {
            log.warn("Invalid JWT token! ", e)
            setResponse(response, "your token is malformed")
        } catch (e: UnsupportedJwtException) {
            log.warn("Invalid JWT token! ", e)
            setResponse(response, "your token is malformed")
        } catch (e: IllegalArgumentException) {
            log.warn("Invalid JWT token! ", e)
            setResponse(response, "your token is malformed")
        } catch (e: ExpiredJwtException) {
            log.debug("Security exception for user {} - {}", e.claims.subject, e.message, e)
            setResponse(response, "your token is expired")
        }
    }

    private fun setResponse(httpServletResponse: HttpServletResponse, message: String) {
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
        httpServletResponse.setHeader("WWW-Authenticate", "error=\"invalid_token\", error_description=\"$message\"")
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken: String = request.getHeader(JwtConfigurer.AUTHORIZATION_HEADER)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConfigurer.BEARER_PREFIX)) {
            bearerToken.substring(JwtConfigurer.BEARER_PREFIX.length, bearerToken.length)
        } else null
    }
}