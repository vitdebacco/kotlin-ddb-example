package com.example.kotlinddb.repositories

import com.example.kotlinddb.App
import com.example.kotlinddb.config.DynamoDBConfig
import com.example.kotlinddb.helpers.RoasterTestData
import io.jooby.JoobyTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
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

    @Nested
    inner class Create {

        @Test
        fun `inserts expected roaster`() {
            val roasterCreateRequest = RoasterTestData.createRequestInstance()
            val result = repository.create(roasterCreateRequest)

            assertEquals("new-coffee-roaster", result.id)
            assertEquals(roasterCreateRequest.name, result.name)
            assertEquals(roasterCreateRequest.url, result.url)
            assertEquals(roasterCreateRequest.status, result.status)

            // cleanup
            repository.delete(result.id)
        }

        @Test
        fun `throws ConditionalCheckFailedException when roaster already exists`() {
            val roasterCreateRequest = RoasterTestData.createRequestInstance()
            val result = repository.create(roasterCreateRequest)

            assertEquals("new-coffee-roaster", result.id)
            assertEquals(roasterCreateRequest.name, result.name)
            assertEquals(roasterCreateRequest.url, result.url)
            assertEquals(roasterCreateRequest.status, result.status)

            assertThrows<ConditionalCheckFailedException> { repository.create(roasterCreateRequest) }

            // cleanup
            repository.delete(result.id)
        }
    }

    @Nested
    inner class Update {

        @Test
        fun `updates roaster`() {
            val roasterCreateRequest = RoasterTestData.createRequestInstance()
            val roasterUpdateRequest = RoasterTestData.updateRequestInstance()

            val roaster = repository.create(roasterCreateRequest)

            repository.update(roaster.id, roasterUpdateRequest)

            val updated = repository.findById(roaster.id)

            assertNotNull(updated)
            assertEquals(roasterUpdateRequest.name, updated.name)
            assertEquals(roasterUpdateRequest.status, updated.status)
            assertEquals(roasterUpdateRequest.url, updated.url)
            assertEquals(roaster.createdAt, updated.createdAt)
            assertNotNull(updated.updatedAt)

            repository.delete(roaster.id)
        }

        @Test
        fun `allows partial updates`() {
            val roasterCreateRequest = RoasterTestData.createRequestInstance()
            val roasterUpdateRequest = RoasterTestData.updateRequestInstance(
                name = null,
                url = null,
                status = "test"
            )

            val roaster = repository.create(roasterCreateRequest)

            repository.update(roaster.id, roasterUpdateRequest)

            val updated = repository.findById(roaster.id)

            assertNotNull(updated)
            assertEquals(roasterUpdateRequest.status, updated.status)
            assertEquals(roasterCreateRequest.name, updated.name)
            assertEquals(roasterCreateRequest.url, updated.url)
            assertEquals(roaster.createdAt, updated.createdAt)
            assertNotNull(updated.updatedAt)

            repository.delete(roaster.id)
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `deletes roaster by ID`() {
            val roasterCreateRequest = RoasterTestData.createRequestInstance()
            val testRoaster = repository.create(roasterCreateRequest)

            repository.delete(testRoaster.id)

            val result = repository.findById(testRoaster.id)

            assertNull(result)
        }

        @Test
        fun `throws ConditionalCheckFailedException when roaster does not exist`() {
            assertThrows<ConditionalCheckFailedException> { repository.delete("does-not-exist") }
        }
    }
}
