package com.example.kotlinddb.models.data

import java.net.URL
import java.time.Instant

data class Offering(
    val id: String,
    val name: String,
    val origin: String,
    val description: String,
    val tastingNotes: Set<String> = emptySet(),
    val roaster: Map<String, String> = emptyMap(),
    val price: String,
    val url: URL,
    val status: String,
    val createdAt: Instant,
    val additionalDetails: Map<String, String> = emptyMap()
)
