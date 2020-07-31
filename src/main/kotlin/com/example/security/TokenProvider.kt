package com.example.security

import com.example.security.dto.JwtPayload
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import java.lang.IllegalArgumentException
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.Optional

class TokenProvider(@Value("\${jwt.secret}")var secretKey: String) {

    private val mapper = ObjectMapper()

    fun createToken(issuer: String, audience: String, tz: ZoneId, validity: ZonedDateTime, payload: JwtPayload): String {
        return createToken(issuer, audience, tz, Optional.ofNullable(validity), payload)
    }

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .body
        val json = claims.get(JwtConfigurer.CLAIM_KEY, String::class.java) ?: throw IllegalArgumentException()
        val jwtPayload = mapper.readValue(json, JwtPayload::class.java)
        return UsernamePasswordAuthenticationToken(jwtPayload, "", jwtPayload.authorities)
    }

    fun validateToken(authToken: String) {
        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken)
    }

    private fun createToken(issuer: String, audience: String, tz: ZoneId, validity: Optional<ZonedDateTime>, jwtPayload: JwtPayload): String {
        val now = ZonedDateTime.ofInstant(Instant.now(), tz)
        val currentDate = Date(now.toEpochSecond() * 1000L)
        val builder: JwtBuilder = Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .setSubject(jwtPayload.name)
            .setIssuer(issuer)
            .setAudience(audience)
            .setIssuedAt(currentDate)
            .setNotBefore(currentDate)
            .claim(JwtConfigurer.CLAIM_KEY, mapper.writer().writeValueAsString(jwtPayload))
        validity.ifPresent { e: ZonedDateTime -> builder.setExpiration(Date(e.toEpochSecond() * 1000L)) }
        return builder.compact()
    }
}