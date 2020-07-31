package com.example.security.util

import com.example.security.dto.JwtPayload
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional

object SecurityHelper {
    fun getPayload(): Optional<JwtPayload> {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal
        return if (principal is JwtPayload) Optional.ofNullable(principal) else Optional.empty()
    }
}