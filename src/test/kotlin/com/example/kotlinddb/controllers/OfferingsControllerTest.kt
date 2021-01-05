package com.example.kotlinddb.controllers

import com.example.kotlinddb.helpers.OfferingTestData
import com.example.kotlinddb.models.filters.OriginFilter
import com.example.kotlinddb.repositories.OfferingRepository
import io.jooby.Context
import io.jooby.exception.StatusCodeException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OfferingsControllerTest {
    companion object {
        private val testOffering = OfferingTestData.instance(roasterIdOnly = true)

        private val originFilterNoRoasterId = OriginFilter(originName = testOffering.origin)
        private val originFilter = OriginFilter(originName = testOffering.origin, roasterId = "fake")
    }

    private val mockRepository = mockk<OfferingRepository>() {
        every { findAllByOrigin(originFilterNoRoasterId) }.returns(listOf(testOffering))
        every { findAllByOrigin(originFilter) }.returns(emptyList())
    }

    val controller = OfferingsController(mockRepository)

    @Nested
    inner class Index {

        @Test
        fun `returns expected offerings without roasterId filter parameter`() {
            val mockCtx =
                mockk<Context> { every { queryMap() }.returns(mapOf("origin_name" to testOffering.origin)) }

            val result = controller.index(mockCtx)

            assertEquals(1, result.size)

            // Ensure the proper function is called. This would be important if functionality were added that executed
            // a query that didn't use this index.
            verify(exactly = 1) { mockRepository.findAllByOrigin(originFilterNoRoasterId) }
        }

        @Test
        // This is really just a shady way to test that `OriginFilter` is being built as expected (with roasterId.
        fun `returns expected offerings with roasterId filter parameter`() {
            val mockCtx =
                mockk<Context> {
                    every { queryMap() }.returns(mapOf("origin_name" to testOffering.origin, "roaster_id" to "fake"))
                }

            val result = controller.index(mockCtx)

            assertTrue(result.isEmpty())
            verify(exactly = 1) { mockRepository.findAllByOrigin(originFilter) }
        }

        @Test
        fun `throws expected StatusCodeException when origin_name not included`() {
            val mockCtx =
                mockk<Context> { every { queryMap() }.returns(emptyMap()) }

            val result = assertThrows<StatusCodeException> { controller.index(mockCtx) }

            assertEquals(400, result.statusCode.value())
            assertEquals("param 'origin_name' is required", result.message)
        }
    }
}
