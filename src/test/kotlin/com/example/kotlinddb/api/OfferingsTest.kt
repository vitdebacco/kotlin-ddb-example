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
class OfferingsTest {

    companion object {
        private const val PATH = "${TestConstants.BASE_URL}/offerings"
        private const val VALID_ORIGIN = "ecuador"
        private const val EMPTY_ORIGIN = "alaska"
        private const val ROASTER = "counter-culture-coffee"
        private const val NORESULTS_ROASTER = "scratch-living"
    }

    @Nested
    inner class Index {

        @Nested
        inner class FilterByOrigin {

            @Test
            fun `returns expected offerings`() {
                Given {
                    contentType(ContentType.JSON)
                    accept(ContentType.JSON)
                    queryParam("origin_name", VALID_ORIGIN)
                } When {
                    get(PATH)
                } Then {
                    statusCode(200)
                    body("size()", equalTo(2))
                }
            }

            @Test
            fun `responds with 400 when no 'origin_name' query param`() {
                Given {
                    contentType(ContentType.JSON)
                    accept(ContentType.JSON)
                } When {
                    get(PATH)
                } Then {
                    statusCode(400)
                    body("message", equalTo("param 'origin_name' is required"))
                }
            }

            @Test
            fun `returns empty array for origin with no offerings`() {
                Given {
                    contentType(ContentType.JSON)
                    accept(ContentType.JSON)
                    queryParam("origin_name", EMPTY_ORIGIN)
                } When {
                    get(PATH)
                } Then {
                    statusCode(200)
                    body("size()", equalTo(0))
                }
            }

            @Nested
            inner class FilterByRoaster {

                @Test
                fun `returns expected offerings`() {
                    Given {
                        contentType(ContentType.JSON)
                        accept(ContentType.JSON)
                        queryParam("origin_name", VALID_ORIGIN)
                        queryParam("roaster_id", ROASTER)
                    } When {
                        get(PATH)
                    } Then {
                        statusCode(200)
                        body("size()", equalTo(1))
                    }
                }

                @Test
                fun `returns empty array for roaster with no offerings`() {
                    Given {
                        contentType(ContentType.JSON)
                        accept(ContentType.JSON)
                        queryParam("origin_name", VALID_ORIGIN)
                        queryParam("roaster_id", NORESULTS_ROASTER)
                    } When {
                        get(PATH)
                    } Then {
                        statusCode(200)
                        body("size()", equalTo(0))
                    }
                }

                @Test
                fun `returns empty array for origin and roaster with no offerings`() {
                    Given {
                        contentType(ContentType.JSON)
                        accept(ContentType.JSON)
                        queryParam("origin_name", EMPTY_ORIGIN)
                        queryParam("roaster_id", NORESULTS_ROASTER)
                    } When {
                        get(PATH)
                    } Then {
                        statusCode(200)
                        body("size()", equalTo(0))
                    }
                }
            }
        }
    }
}
