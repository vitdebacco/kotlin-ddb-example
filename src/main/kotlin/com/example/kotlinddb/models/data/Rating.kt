package com.example.kotlinddb.models.data

import java.time.Instant
import java.util.UUID

data class Rating(
    val id: UUID = UUID.randomUUID(),
    val rating: Int,
    val comment: String,
    val createdAt: Instant = Instant.now()
)
