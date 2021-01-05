package com.example.kotlinddb.api

import com.example.kotlinddb.App
import com.example.kotlinddb.helpers.TestConstants
import io.jooby.JoobyTest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test

@JoobyTest(App::class)
class HealthCheckTest {

    @Test
    fun `returns 200 and expected payload`() {
        Given {
            contentType(ContentType.JSON)
            accept(ContentType.JSON)
        } When {
            get("${TestConstants.BASE_URL}/health_check")
        } Then {
            statusCode(200)
            body("app", equalTo("up"))
        }
    }
}
