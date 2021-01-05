package com.example.kotlinddb.helpers

import com.example.kotlinddb.models.data.Roaster
import java.net.URL
import java.time.Instant

object RoasterTestData {

    fun instance(
        id: String = "test-roaster",
        name: String = "Test Roaster",
        url: URL = URL("https://www.testroaster.com"),
        status: String = "active",
        createdAt: Instant = Instant.now()
    ): Roaster {
        return Roaster(
            id = id,
            name = name,
            url = url,
            status = status,
            createdAt = createdAt
        )
    }
}
