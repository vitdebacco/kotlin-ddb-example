package com.example.kotlinddb.models.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import java.net.URL
import java.time.Instant

data class Roaster(
    val id: String,
    val name: String,
    val url: URL,
    val status: String,
    val createdAt: Instant,

    @JsonInclude(Include.NON_NULL)
    val updatedAt: Instant? = null,

    @JsonInclude(Include.NON_NULL)
    var deletedAt: Instant? = null
)
