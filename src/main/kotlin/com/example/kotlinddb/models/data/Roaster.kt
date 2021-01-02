package com.example.kotlinddb.models.data

import java.net.URL
import java.time.Instant

data class Roaster(
    val id: String,
    val name: String,
    val url: URL,
    val status: String,
    val createdAt: Instant
)
