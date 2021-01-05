package com.example.kotlinddb.repositories

import com.example.kotlinddb.App
import com.example.kotlinddb.config.DynamoDBConfig
import io.jooby.JoobyTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@JoobyTest(App::class)
class RoasterRepositoryTest {

    companion object {
        private val dynamoDBClient = DynamoDBConfig().client()
        private val repository = RoasterRepository(dynamoDBClient)
    }

    @Nested
    inner class FindById {

        @Test
        fun `returns expected roaster`() {
            val roaster = repository.findById("counter-culture-coffee")

            assertNotNull(roaster)
            assertEquals("counter-culture-coffee", roaster.id)
            assertEquals("Counter Culture Coffee", roaster.name)
            assertEquals("active", roaster.status)
            assertEquals("https://counterculturecoffee.com/", roaster.url.toString())
        }

        @Test
        fun `returns null when no roaster found for ID`() {
            val roaster = repository.findById("does-not-exist-coffee")

            assertNull(roaster)
        }
    }
}
