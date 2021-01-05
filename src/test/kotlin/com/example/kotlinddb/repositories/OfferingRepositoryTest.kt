package com.example.kotlinddb.repositories

import com.example.kotlinddb.App
import com.example.kotlinddb.config.DynamoDBConfig
import com.example.kotlinddb.exceptions.NotFoundException
import com.example.kotlinddb.models.filters.OriginFilter
import io.jooby.JoobyTest
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
        fun `throws NotFoundException when roaster does not exist`() {
            val e = assertThrows<NotFoundException> { repository.findAllByRoaster("does-not-exist-coffee") }

            assertEquals("roaster 'does-not-exist-coffee' not found", e.message)
        }
    }

    @Nested
    inner class FindByOrigin {

        @Test
        fun `returns expected offerings`() {
            val offerings = repository.findAllByOrigin(OriginFilter(originName = "ecuador"))

            assertEquals(2, offerings.size)
        }

        @Nested
        inner class FilterByRoaster {

            @Test
            fun `returns expected offerings`() {
                val filter = OriginFilter(
                    originName = "ecuador",
                    roasterId = "counter-culture-coffee"
                )

                val offerings = repository.findAllByOrigin(filter)

                assertEquals(1, offerings.size)
            }
        }
    }
}
