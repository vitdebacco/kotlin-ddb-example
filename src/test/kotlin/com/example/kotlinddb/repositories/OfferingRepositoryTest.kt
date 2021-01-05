package com.example.kotlinddb.repositories

import com.example.kotlinddb.App
import com.example.kotlinddb.config.DynamoDBConfig
import io.jooby.JoobyTest
import io.jooby.exception.StatusCodeException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@JoobyTest(App::class)
class OfferingRepositoryTest {

    companion object {
        private val dynamoDBClient = DynamoDBConfig().client()
        private val repository = OfferingRepository(dynamoDBClient)
    }

    @Nested
    inner class FindByRoaster {

        @Test
        fun `returns expected of offerings`() {
            val offerings = repository.findAllByRoaster("counter-culture-coffee")

            assertEquals(3, offerings.size)
        }

        @Test
        fun `returns empty list when roaster has no offerings`() {
            val offerings = repository.findAllByRoaster("scratch-living")

            assertTrue(offerings.isEmpty())
        }

        @Test
        fun `throws StatusCodeException when roaster does not exist`() {
            val e = assertThrows<StatusCodeException> { repository.findAllByRoaster("does-not-exist-coffee") }

            assertEquals(404, e.statusCode.value())
            assertEquals("roaster 'does-not-exist-coffee' not found", e.message)
        }
    }

    @Nested
    inner class FindByOrigin {

        @Test
        fun `returns expected offerings`() {
            val offerings = repository.findAllByOrigin(mapOf("origin_name" to "ecuador"))

            assertEquals(2, offerings.size)
        }

        @Test
        fun `origin_name param is required`() {
            val e = assertThrows<StatusCodeException> { repository.findAllByOrigin(emptyMap()) }

            assertEquals(400, e.statusCode.value())
            assertEquals("param 'origin_name' is required", e.message)
        }

        @Nested
        inner class FilterByRoaster {

            @Test
            fun `returns expected offerings`() {
                val params = mapOf(
                    "origin_name" to "ecuador",
                    "roaster_id" to "counter-culture-coffee"
                )

                val offerings = repository.findAllByOrigin(params)

                assertEquals(1, offerings.size)
            }
        }
    }
}
