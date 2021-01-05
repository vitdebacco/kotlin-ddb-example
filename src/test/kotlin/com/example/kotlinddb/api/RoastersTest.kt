package com.example.kotlinddb.api

import com.example.kotlinddb.App
import com.example.kotlinddb.helpers.TestConstants
import io.jooby.JoobyTest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@JoobyTest(App::class)
class RoastersTest {

    companion object {
        private val validRoasterId = "counter-culture-coffee"
        private val invalidRoasterId = "fake-coffee"
    }

    @Nested
    inner class Show {

        @Test
        fun `returns expected roaster`() {
            Given {
                contentType(ContentType.JSON)
                accept(ContentType.JSON)
            } When {
                get("${TestConstants.BASE_URL}/roasters/$validRoasterId")
            } Then {
                statusCode(200)
                body("size()", equalTo(5))
            }
        }

        @Test
        fun `returns 404 when roaster not found`() {
            Given {
                contentType(ContentType.JSON)
                accept(ContentType.JSON)
            } When {
                get("${TestConstants.BASE_URL}/roasters/$invalidRoasterId")
            } Then {
                statusCode(404)
                body("message", equalTo("roaster '$invalidRoasterId' not found"))
            }
        }
    }
}
