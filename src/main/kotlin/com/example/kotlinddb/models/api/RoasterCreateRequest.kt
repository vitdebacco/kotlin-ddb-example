package com.example.kotlinddb.models.api

import java.net.URL

data class RoasterCreateRequest(
    val name: String,
    val url: URL,
    val status: String = "active"
)
