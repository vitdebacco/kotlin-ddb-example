package com.example.kotlinddb.api

import com.example.kotlinddb.App
import com.example.kotlinddb.helpers.TestConstants
import io.jooby.JoobyTest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test

@JoobyTest(App::class)
class RoastersShowTest {

    companion object {
        private const val validRoasterId = "counter-culture-coffee"
        private const val invalidRoasterId = "fake-coffee"
    }

    @Test
    fun `returns expected roaster`() {
        Given {
            contentType(ContentType.JSON)
            accept(ContentType.JSON)
        } When {
            get("${TestConstants.BASE_URL}/roasters/$validRoasterId")
        } Then {
            statusCode(200)
            body("size()", CoreMatchers.equalTo(5))
            body("id", CoreMatchers.equalTo(validRoasterId))
            body("name", CoreMatchers.equalTo("Counter Culture Coffee"))
            body("url", CoreMatchers.equalTo("https://counterculturecoffee.com/"))
            body("status", CoreMatchers.equalTo("active"))
            body("created_at", CoreMatchers.notNullValue())
            body("updated_at", CoreMatchers.nullValue())
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
            body("statusCode", CoreMatchers.equalTo(404))
            body("message", CoreMatchers.equalTo("roaster '$invalidRoasterId' not found"))
            body("reason", CoreMatchers.equalTo("Not Found"))
        }
    }
}
