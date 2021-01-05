package com.example.kotlinddb.models.filters

data class OriginFilter(
    val originName: String,
    val roasterId: String? = null
)
