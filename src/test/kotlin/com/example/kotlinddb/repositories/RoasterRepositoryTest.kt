package com.example.kotlinddb.repositories

import com.example.kotlinddb.App
import com.example.kotlinddb.config.DynamoDBConfig
import io.jooby.JoobyTest
import io.jooby.exception.StatusCodeException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

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
            val result = repository.findById("counter-culture-coffee")

            assertEquals("counter-culture-coffee", result.id)
            assertEquals("Counter Culture Coffee", result.name)
            assertEquals("active", result.status)
            assertEquals("https://counterculturecoffee.com/", result.url.toString())
        }

        @Test
        fun `throws StatusCodeException for invalid ID`() {
            val e = assertThrows<StatusCodeException> { repository.findById("does-not-exist-coffee") }

            assertEquals(404, e.statusCode.value())
            assertEquals("roaster 'does-not-exist-coffee' not found", e.message)
        }
    }
}
