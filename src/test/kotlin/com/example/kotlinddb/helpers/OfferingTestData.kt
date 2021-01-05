package com.example.kotlinddb.helpers

import com.example.kotlinddb.models.data.Offering
import java.net.URL
import java.time.Instant

object OfferingTestData {

    fun instance(
        testTimeStamp: Instant = Instant.now(),
        id: String = "muda",
        name: String = "Muda",
        origin: String = "Ethiopia",
        description: String = "Delicious coffee from Ethiopia!",
        tastingNotes: Set<String> = setOf("Lime", "Floral", "Dark Honey"),
        roaster: Map<String, String> = mapOf(
            "id" to "counter-culture-coffee",
            "name" to "Counter Culture Coffee",
            "status" to "active",
            "url" to "https://counterculturecoffee.com/",
            "createdAt" to testTimeStamp.toString()
        ),
        roasterIdOnly: Boolean = false,
        price: String = "18.00",
        url: URL = URL("https://counterculturecoffee.com/shop/coffee/muda"),
        status: String = "active",
        createdAt: Instant = testTimeStamp,
        additionalDetails: Map<String, String> = emptyMap()
    ): Offering {
        return Offering(
            id = id,
            name = name,
            origin = origin,
            description = description,
            tastingNotes = tastingNotes,
            roaster = roaster(roaster, roasterIdOnly),
            price = price,
            url = url,
            status = status,
            createdAt = createdAt,
            additionalDetails = additionalDetails
        )
    }

    private fun roaster(roaster: Map<String, String>, roasterIdOnly: Boolean): Map<String, String> {
        return if (roasterIdOnly) {
            mapOf("roasterId" to roaster["id"]!!)
        } else {
            roaster
        }
    }
}
