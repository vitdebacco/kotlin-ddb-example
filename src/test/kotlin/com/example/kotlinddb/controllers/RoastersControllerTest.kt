package com.example.kotlinddb.controllers

import com.example.kotlinddb.helpers.RoasterTestData
import com.example.kotlinddb.models.api.RoasterCreateRequest
import com.example.kotlinddb.models.api.RoasterUpdateRequest
import com.example.kotlinddb.models.data.Roaster
import com.example.kotlinddb.repositories.RoasterRepository
import io.jooby.Context
import io.jooby.StatusCode
import io.jooby.exception.StatusCodeException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
import software.amazon.awssdk.services.dynamodb.model.InternalServerErrorException
import software.amazon.awssdk.services.dynamodb.model.RequestLimitExceededException
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Required for @BeforeAll
class RoastersControllerTest {
    companion object {
        private const val DOES_NOT_EXIST_ROASTER_ID = "does-not-exist-coffee"
        private val testRoaster = RoasterTestData.instance()
    }

    @BeforeAll
    fun setupAll() {
        // Stop the system clock!
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        mockkStatic(Clock::class)
        every { Clock.systemUTC() } returns fixedClock
    }

    @Nested
    inner class Show {
        private val mockRepository = mockk<RoasterRepository> {
            every { findById(testRoaster.id) }.returns(testRoaster)
            every { findById(DOES_NOT_EXIST_ROASTER_ID) }.returns(null)
        }

        private val controller = RoastersController(mockRepository)

        @Test
        fun `returns expected Roaster`() {
            val mockCtx = mockk<Context> { every { path("id").value() }.returns(testRoaster.id) }

            val result = controller.show(mockCtx)

            assertEquals(testRoaster, result)
        }

        @Test
        fun `throws expected StatusCodeException when roaster not found`() {
            val mockCtx = mockk<Context> { every { path("id").value() }.returns(DOES_NOT_EXIST_ROASTER_ID) }

            val result = assertThrows<StatusCodeException> { controller.show(mockCtx) }

            assertEquals(404, result.statusCode.value())
            assertEquals("roaster '$DOES_NOT_EXIST_ROASTER_ID' not found", result.message)
        }
    }

    @Nested
    inner class Create {
        private val alreadyExistsCoffee = RoasterTestData.createRequestInstance(name = "Already Exists Coffee")
        private val tooManyCoffees = RoasterTestData.createRequestInstance(name = "Too Many Coffees")
        private val defaultCoffee = RoasterTestData.createRequestInstance(name = "Default Coffee")

        private val roasterCreateRequest = RoasterTestData.createRequestInstance()
        private val createdRoaster = Roaster(
            id = "new-coffee-roaster",
            name = roasterCreateRequest.name,
            url = roasterCreateRequest.url,
            status = roasterCreateRequest.status,
            createdAt = Instant.now()
        )

        private val mockRepository = mockk<RoasterRepository> {
            every { create(roasterCreateRequest) }.returns(createdRoaster)
            every { create(alreadyExistsCoffee) }.throws(ConditionalCheckFailedException.builder().build())
            every { create(tooManyCoffees) }.throws(RequestLimitExceededException.builder().build())
            every { create(defaultCoffee) }.throws(InternalServerErrorException.builder().build())
        }

        private val controller = RoastersController(mockRepository)

        @Test
        fun `creates new roaster`() {
            val mockCtx = mockk<Context> {
                every { body(RoasterCreateRequest::class.java) }.returns(roasterCreateRequest)
                every { setResponseCode(StatusCode.CREATED) }.returns(this)
            }

            val result = controller.create(mockCtx)

            assertEquals(createdRoaster, result)
            verify(exactly = 1) { mockCtx.setResponseCode(StatusCode.CREATED) }
        }

        @Test
        fun `throws expected StatusCodeException when roaster already exists`() {
            val mockCtx = mockk<Context> {
                every { body(RoasterCreateRequest::class.java) }.returns(alreadyExistsCoffee)
            }

            val result = assertThrows<StatusCodeException> { controller.create(mockCtx) }

            assertEquals(StatusCode.CONFLICT.value(), result.statusCode.value())
            assertEquals("roaster '${alreadyExistsCoffee.name}' already exists", result.message)
        }

        @Test
        fun `throws expected StatusCodeException when request limit exceeded`() {
            val mockCtx = mockk<Context> {
                every { body(RoasterCreateRequest::class.java) }.returns(tooManyCoffees)
            }

            val result = assertThrows<StatusCodeException> { controller.create(mockCtx) }

            assertEquals(StatusCode.TOO_MANY_REQUESTS.value(), result.statusCode.value())
        }

        @Test
        fun `throws expected StatusCodeException unknown error occurs`() {
            val mockCtx = mockk<Context> {
                every { body(RoasterCreateRequest::class.java) }.returns(defaultCoffee)
            }

            val result = assertThrows<StatusCodeException> { controller.create(mockCtx) }

            assertEquals(StatusCode.SERVER_ERROR.value(), result.statusCode.value())
        }
    }

    @Nested
    inner class Update {
        private val doesNotExistCoffee = RoasterTestData.updateRequestInstance(name = "Does Not Exist Coffee")
        private val doesNotExistCoffeeId = "does-not-exist-coffee"

        private val tooManyCoffees = RoasterTestData.updateRequestInstance(name = "Too Many Coffees")
        private val defaultCoffee = RoasterTestData.updateRequestInstance(name = "Default Coffee")

        private val updateRequest = RoasterTestData.updateRequestInstance()
        private val updatedRoaster = RoasterTestData.instance(
            name = updateRequest.name!!,
            url = updateRequest.url!!,
            status = updateRequest.status!!,
            updatedAt = Instant.now()
        )

        private val mockRepository = mockk<RoasterRepository> {
            every { findById(updatedRoaster.id) }.returns(updatedRoaster)
            every { update(updatedRoaster.id, updateRequest) }.returns(Unit)
            every { update(updatedRoaster.id, doesNotExistCoffee) }.throws(
                ConditionalCheckFailedException.builder().build()
            )
            every { update(updatedRoaster.id, tooManyCoffees) }.throws(RequestLimitExceededException.builder().build())
            every { update(updatedRoaster.id, defaultCoffee) }.throws(InternalServerErrorException.builder().build())
        }

        private val controller = RoastersController(mockRepository)

        @Test
        fun `updates existing roaster`() {
            val mockCtx = mockk<Context> {
                every { path("id").value() }.returns(updatedRoaster.id)
                every { body(RoasterUpdateRequest::class.java) }.returns(updateRequest)
            }

            val result = controller.update(mockCtx)

            assertEquals(updatedRoaster, result)
            verify(exactly = 1) { mockRepository.findById(updatedRoaster.id) }
        }

        @Test
        fun `throws expected StatusCodeException when roaster does not exist`() {
            val mockCtx = mockk<Context> {
                every { path("id").value() }.returns(updatedRoaster.id)
                every { body(RoasterUpdateRequest::class.java) }.returns(doesNotExistCoffee)
            }

            val result = assertThrows<StatusCodeException> { controller.update(mockCtx) }

            assertEquals(StatusCode.NOT_FOUND.value(), result.statusCode.value())
            assertEquals("roaster '${updatedRoaster.id}' not found", result.message)
        }

        @Test
        fun `throws expected StatusCodeException when request limit exceeded`() {
            val mockCtx = mockk<Context> {
                every { path("id").value() }.returns(updatedRoaster.id)
                every { body(RoasterUpdateRequest::class.java) }.returns(tooManyCoffees)
            }

            val result = assertThrows<StatusCodeException> { controller.update(mockCtx) }

            assertEquals(StatusCode.TOO_MANY_REQUESTS.value(), result.statusCode.value())
        }

        @Test
        fun `throws expected StatusCodeException unknown error occurs`() {
            val mockCtx = mockk<Context> {
                every { path("id").value() }.returns(updatedRoaster.id)
                every { body(RoasterUpdateRequest::class.java) }.returns(defaultCoffee)
            }

            val result = assertThrows<StatusCodeException> { controller.update(mockCtx) }

            assertEquals(StatusCode.SERVER_ERROR.value(), result.statusCode.value())
        }
    }

    @Nested
    inner class Delete {
        private val roaster = RoasterTestData.instance(deletedAt = Instant.now())
        private val doesNotExistCoffeeId = "does-not-exist-coffee"
        private val tooManyCoffeesId = "too-many-coffees"
        private val defaultCoffeeId = "default-coffee"

        private val mockRepository = mockk<RoasterRepository> {
            every { findById(roaster.id) }.returns(roaster)
            every { findById(doesNotExistCoffeeId) }.returns(null)
            every { findById(tooManyCoffeesId) }.returns(roaster)
            every { findById(defaultCoffeeId) }.returns(roaster)
            every { delete(roaster.id) }.returns(Unit)
            every { delete(doesNotExistCoffeeId) }.throws(ConditionalCheckFailedException.builder().build())
            every { delete(tooManyCoffeesId) }.throws(RequestLimitExceededException.builder().build())
            every { delete(defaultCoffeeId) }.throws(InternalServerErrorException.builder().build())
        }

        private val controller = RoastersController(mockRepository)

        @Test
        fun `deletes existing roaster`() {
            val mockCtx = mockk<Context> {
                every { path("id").value() }.returns(roaster.id)
            }

            val result = controller.delete(mockCtx)

            assertEquals(roaster, result)
            verify(exactly = 1) { mockRepository.delete(roaster.id) }
        }

        @Test
        fun `throws expected StatusCodeException when roaster does not exist`() {
            val mockCtx = mockk<Context> {
                every { path("id").value() }.returns(doesNotExistCoffeeId)
            }

            val result = assertThrows<StatusCodeException> { controller.delete(mockCtx) }

            assertEquals(StatusCode.NOT_FOUND.value(), result.statusCode.value())
            assertEquals("roaster '$DOES_NOT_EXIST_ROASTER_ID' not found", result.message)
        }

        @Test
        fun `throws expected StatusCodeException when request limit exceeded`() {
            val mockCtx = mockk<Context> {
                every { path("id").value() }.returns(tooManyCoffeesId)
            }

            val result = assertThrows<StatusCodeException> { controller.delete(mockCtx) }

            assertEquals(StatusCode.TOO_MANY_REQUESTS.value(), result.statusCode.value())
        }

        @Test
        fun `throws expected StatusCodeException unknown error occurs`() {
            val mockCtx = mockk<Context> {
                every { path("id").value() }.returns(defaultCoffeeId)
            }

            val result = assertThrows<StatusCodeException> { controller.delete(mockCtx) }

            assertEquals(StatusCode.SERVER_ERROR.value(), result.statusCode.value())
        }
    }
}
