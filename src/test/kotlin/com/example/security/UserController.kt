package com.example.security

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority(T(com.example.security.dto.Right).READ_USER.name())")
    fun getUser(@PathVariable id: Long): User {
        if (id == 1L) {
            return User(1L, "Diana")
        } else throw UserNotFoundException("User doesn't exist.")
    }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException(message: String?): Exception(message)

data class User(val id: Long, val name: String)