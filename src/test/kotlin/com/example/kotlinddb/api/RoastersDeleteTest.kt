package com.example.kotlinddb.api

import com.example.kotlinddb.App
import com.example.kotlinddb.config.DynamoDBConfig
import com.example.kotlinddb.helpers.RoasterTestData
import com.example.kotlinddb.helpers.TestConstants
import com.example.kotlinddb.repositories.RoasterRepository
import io.jooby.JoobyTest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@JoobyTest(App::class)
class RoastersDeleteTest {
    private val repository = RoasterRepository(DynamoDBConfig().client())

    @Nested
    inner class Valid {
        private val createRequest = RoasterTestData.createRequestInstance()
        private val id = createRequest.name.toLowerCase().replace(" ", "-")

        @BeforeEach
        fun setup() {
            repository.create(createRequest)
        }

        @AfterEach
        fun cleanup() {
            val roaster = repository.findById(id)
            if (roaster != null) {
                repository.delete(id)
            }
        }

        @Test
        fun `deletes roaster`() {
            Given {
                contentType(ContentType.JSON)
                accept(ContentType.JSON)
            } When {
                delete("${TestConstants.BASE_URL}/roasters/$id")
            } Then {
                statusCode(200)
                body("size()", CoreMatchers.equalTo(6))
                body("id", CoreMatchers.equalTo(id))
                body("name", CoreMatchers.equalTo(createRequest.name))
                body("url", CoreMatchers.equalTo(createRequest.url.toString()))
                body("status", CoreMatchers.equalTo(createRequest.status))
                body("created_at", CoreMatchers.notNullValue())
                body("deleted_at", CoreMatchers.notNullValue())
            }
        }
    }

    @Nested
    inner class DoesNotExist {

        @Test
        fun `returns 404 Not Found`() {
            val id = "does-not-exist-coffee"

            Given {
                contentType(ContentType.JSON)
                accept(ContentType.JSON)
            } When {
                delete("${TestConstants.BASE_URL}/roasters/$id")
            } Then {
                statusCode(404)
                body("statusCode", CoreMatchers.equalTo(404))
                body("message", CoreMatchers.equalTo("roaster '$id' not found"))
                body("reason", CoreMatchers.equalTo("Not Found"))
            }
        }
    }
}
