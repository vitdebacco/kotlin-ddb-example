package com.example.kotlinddb.models.api

import java.net.URL

data class RoasterUpdateRequest(
    val name: String? = null,
    val url: URL? = null,
    val status: String? = null
)
