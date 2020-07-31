package com.example.security

import com.example.security.dto.Authority
import com.example.security.dto.JwtPayload
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@ActiveProfiles("test")
@SpringBootTest(classes = [Application::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractIntegrationTest {
    @Value("\${local.server.port}")
    private val serverPort = 0

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @BeforeEach
    fun setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = serverPort
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    protected fun getToken(name: String, authorities: Set<Authority>): String {
        val tz = ZoneId.of("Europe/Berlin")
        val validity = ZonedDateTime.now(tz).plus(1, ChronoUnit.DAYS)
        return tokenProvider.createToken("IT", "testers", tz, validity, JwtPayload(name, authorities))
    }
}