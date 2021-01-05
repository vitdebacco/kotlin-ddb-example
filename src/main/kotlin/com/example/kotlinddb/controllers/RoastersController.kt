package com.example.kotlinddb.controllers

import com.example.kotlinddb.models.data.Roaster
import com.example.kotlinddb.repositories.RoasterRepository
import io.jooby.Context
import io.jooby.StatusCode
import io.jooby.exception.StatusCodeException

class RoastersController(
    private val roasterRepository: RoasterRepository
) {

    fun show(ctx: Context): Roaster {
        val id = ctx.path("id").value()

        return roasterRepository.findById(id)
            ?: throw StatusCodeException(StatusCode.NOT_FOUND, "roaster '$id' not found")
    }
}
