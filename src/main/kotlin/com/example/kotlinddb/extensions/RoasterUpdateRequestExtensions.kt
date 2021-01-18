package com.example.kotlinddb.extensions

import com.example.kotlinddb.models.api.RoasterUpdateRequest

fun RoasterUpdateRequest.noChangesRequested(): Boolean {
    return this.name.isNullOrBlank() &&
        this.url == null &&
        this.status.isNullOrBlank()
}
