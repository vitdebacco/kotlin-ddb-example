package com.example.kotlinddb.controllers

import com.example.kotlinddb.exceptions.NotFoundException
import com.example.kotlinddb.helpers.OfferingTestData
import com.example.kotlinddb.repositories.OfferingRepository
import io.jooby.Context
import io.jooby.exception.StatusCodeException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RoasterOfferingsControllerTest {

    companion object {
        private val testOffering = OfferingTestData.instance()
    }

    private val mockRepository = mockk<OfferingRepository>() {
        every { findAllByRoaster(testOffering.id) }.returns(listOf(testOffering))
        every { findAllByRoaster("roaster-with-no-offerings") }.returns(emptyList())
        every { findAllByRoaster("roaster-that-does-not-exist") }
            .throws(NotFoundException("roaster 'roaster-that-does-not-exist' not found"))
    }

    private val controller = RoasterOfferingsController(mockRepository)

    @Nested
    inner class Index {

        @Test
        fun `returns expected offerings`() {
            val mockCtx = mockk<Context> { every { path("id").value() }.returns(testOffering.id) }
            val result = controller.index(mockCtx)

            assertEquals(1, result.size)
            assertEquals(testOffering, result[0])
        }

        @Test
        fun `returns empty list when no results exist for roaster`() {
            val mockCtx = mockk<Context> {
                every { path("id").value() }
                    .returns("roaster-with-no-offerings")
            }

            val result = controller.index(mockCtx)

            assertTrue(result.isEmpty())
        }

        @Test
        fun `throws expected StatusCodeException when roaster does not exist`() {
            val mockCtx = mockk<Context> {
                every { path("id").value() }
                    .returns("roaster-that-does-not-exist")
            }

            val result = assertThrows<StatusCodeException> { controller.index(mockCtx) }

            assertEquals(404, result.statusCode.value())
            assertEquals("roaster 'roaster-that-does-not-exist' not found", result.message)
        }
    }
}
