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
class RoastersCreateTest {
    private val repository = RoasterRepository(DynamoDBConfig().client())
    private val createRequest = RoasterTestData.createRequestInstance()
    private val id = createRequest.name.toLowerCase().replace(" ", "-")

    @Nested
    inner class Valid {

        @AfterEach
        fun cleanup() {
            repository.delete(id)
        }

        @Test
        fun `returns 200 OK and created roaster`() {
            Given {
                contentType(ContentType.JSON)
                accept(ContentType.JSON)
                body(
                    """
                    {
                        "name": "${createRequest.name}",
                        "url": "${createRequest.url}",
                        "status": "${createRequest.status}"
                    }
                    """.trimIndent()
                )
            } When {
                post("${TestConstants.BASE_URL}/roasters")
            } Then {
                statusCode(201)
                body("size()", CoreMatchers.equalTo(5))
                body("id", CoreMatchers.equalTo(id))
                body("name", CoreMatchers.equalTo(createRequest.name))
                body("url", CoreMatchers.equalTo(createRequest.url.toString()))
                body("status", CoreMatchers.equalTo(createRequest.status))
                body("created_at", CoreMatchers.notNullValue())
                body("updated_at", CoreMatchers.nullValue())
            }
        }
    }

    @Nested
    inner class AlreadyExists {

        @BeforeEach
        fun setup() {
            repository.create(createRequest)
        }

        @AfterEach
        fun cleanup() {
            repository.delete(id)
        }

        @Test
        fun `returns 409 Conflict `() {
            Given {
                contentType(ContentType.JSON)
                accept(ContentType.JSON)
                body(
                    """
                    {
                        "name": "${createRequest.name}",
                        "url": "${createRequest.url}",
                        "status": "${createRequest.status}"
                    }
                    """.trimIndent()
                )
            } When {
                post("${TestConstants.BASE_URL}/roasters")
            } Then {
                statusCode(409)
                body("statusCode", CoreMatchers.equalTo(409))
                body("message", CoreMatchers.equalTo("roaster '${createRequest.name}' already exists"))
                body("reason", CoreMatchers.equalTo("Conflict"))
            }
        }
    }
}
