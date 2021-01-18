package com.example.kotlinddb.extensions

import com.example.kotlinddb.models.api.RoasterUpdateRequest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URL
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RoasterUpdateRequestExtensionsTest {

    @Nested
    inner class NoChangesRequested {

        @Test
        fun `returns true when all fields are null`() {
            val req = RoasterUpdateRequest()

            assertTrue(req.noChangesRequested())
        }

        @Test
        fun `returns true when string fields are blank`() {
            val req = RoasterUpdateRequest(
                name = "  ",
                status = "  ",
                url = null
            )

            assertTrue(req.noChangesRequested())
        }

        @Test
        fun `returns false when all fields have a non-empty value`() {
            val req = RoasterUpdateRequest(
                name = "test",
                status = "active",
                url = URL("https://www.test.com")
            )

            assertFalse(req.noChangesRequested())
        }

        @Test
        fun `returns false when one field has a non-empty value`() {
            val req = RoasterUpdateRequest(
                status = "test"
            )

            assertFalse(req.noChangesRequested())
        }
    }
}
