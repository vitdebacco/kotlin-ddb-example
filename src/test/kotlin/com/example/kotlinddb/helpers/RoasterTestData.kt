package com.example.kotlinddb.helpers

import com.example.kotlinddb.models.api.RoasterCreateRequest
import com.example.kotlinddb.models.api.RoasterUpdateRequest
import com.example.kotlinddb.models.data.Roaster
import java.net.URL
import java.time.Instant

object RoasterTestData {

    fun instance(
        id: String = "test-roaster",
        name: String = "Test Roaster",
        url: URL = URL("https://www.testroaster.com"),
        status: String = "active",
        createdAt: Instant = Instant.now(),
        updatedAt: Instant? = null,
        deletedAt: Instant? = null
    ): Roaster {
        return Roaster(
            id = id,
            name = name,
            url = url,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
    }

    fun createRequestInstance(
        name: String = "New Coffee Roaster",
        url: URL = URL("https://newcoffeeroaster.com"),
        status: String = "active"
    ): RoasterCreateRequest {
        return RoasterCreateRequest(
            name = name,
            url = url,
            status = status
        )
    }

    fun updateRequestInstance(
        name: String? = "Updated New Coffee Roaster",
        url: URL? = URL("https://newcoffeeroaster.coffee"),
        status: String? = "active - updated"
    ): RoasterUpdateRequest {
        return RoasterUpdateRequest(
            name = name,
            url = url,
            status = status
        )
    }
}
