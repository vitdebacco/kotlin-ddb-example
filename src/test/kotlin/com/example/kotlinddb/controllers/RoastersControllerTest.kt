package com.example.kotlinddb.controllers

import com.example.kotlinddb.helpers.RoasterTestData
import com.example.kotlinddb.repositories.RoasterRepository
import io.jooby.Context
import io.jooby.exception.StatusCodeException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class RoastersControllerTest {
    companion object {
        private const val DOES_NOT_EXIST_ROASTER_ID = "does-not-exist-coffee"
        private val testRoaster = RoasterTestData.instance()
    }

    private val mockRepository = mockk<RoasterRepository> {
        every { findById(testRoaster.id) }.returns(testRoaster)
        every { findById(DOES_NOT_EXIST_ROASTER_ID) }.returns(null)
    }

    private val controller = RoastersController(mockRepository)

    @Nested
    inner class Show {

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
}
