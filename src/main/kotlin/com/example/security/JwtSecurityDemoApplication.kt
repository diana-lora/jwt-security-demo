package com.example.security

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JwtSecurityDemoApplication

fun main(args: Array<String>) {
	runApplication<JwtSecurityDemoApplication>(*args)
}
