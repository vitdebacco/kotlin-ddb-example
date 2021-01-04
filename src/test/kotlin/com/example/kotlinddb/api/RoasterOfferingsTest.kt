package com.example.kotlinddb.api

import com.example.kotlinddb.App
import com.example.kotlinddb.helpers.TestConstants
import io.jooby.JoobyTest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@JoobyTest(App::class)
class RoasterOfferingsTest {

    companion object {
        private val validRoasterId = "counter-culture-coffee"
        private val emptyRoasterId = "scratch-living"
        private val invalidRoasterId = "fake-coffee"
    }

    @Nested
    inner class Index {

        @Test
        fun `returns expected offerings for roaster`() {
            Given {
                contentType(ContentType.JSON)
                accept(ContentType.JSON)
            } When {
                get("${TestConstants.BASE_URL}/roasters/$validRoasterId/offerings")
            } Then {
                statusCode(200)
                body("size()", CoreMatchers.equalTo(3))
            }
        }

        @Test
        fun `returns empty offerings array for roaster with no offerings`() {
            Given {
                contentType(ContentType.JSON)
                accept(ContentType.JSON)
            } When {
                get("${TestConstants.BASE_URL}/roasters/$emptyRoasterId/offerings")
            } Then {
                statusCode(200)
                body("size()", CoreMatchers.equalTo(0))
            }
        }

        @Test
        fun `returns 404 when roaster not found`() {
            Given {
                contentType(ContentType.JSON)
                accept(ContentType.JSON)
            } When {
                get("${TestConstants.BASE_URL}/roasters/$invalidRoasterId/offerings")
            } Then {
                statusCode(404)
                body("message", CoreMatchers.equalTo("roaster '$invalidRoasterId' not found"))
            }
        }
    }
}