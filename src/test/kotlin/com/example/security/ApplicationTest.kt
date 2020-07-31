package com.example.security

import com.example.security.dto.Authority
import com.example.security.dto.Right
import io.restassured.RestAssured
import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

class ApplicationTest: AbstractIntegrationTest() {

    private fun getUser(token: String, id: Long): ValidatableResponse {
        return RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .`when`()
            .pathParam("id", id)
            .get("/users/{id}")
            .then()
    }

    @Test
    fun getUser() {
        val token = getToken("test", mutableSetOf(Authority(Right.READ_USER)))
        getUser(token, 1L)
            .statusCode(HttpStatus.OK.value())
            .body("id", Matchers.`is`(1))
            .body("name", Matchers.`is`("Diana"))
    }

    @Test
    fun getUser_Unauthorized() {
        getUser("token", 1L)
            .statusCode(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    fun getUser_Forbidden() {
        val token = getToken("test", mutableSetOf(Authority(Right.CREATE_USER)))
        getUser(token, 1L)
            .statusCode(HttpStatus.FORBIDDEN.value())
    }

    @Test
    fun getUser_NotFound() {
        val token = getToken("test", mutableSetOf(Authority(Right.READ_USER)))
        getUser(token, 2L)
            .statusCode(HttpStatus.NOT_FOUND.value())
    }

}
