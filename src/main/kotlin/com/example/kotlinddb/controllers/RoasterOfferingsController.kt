package com.example.kotlinddb.controllers

import com.example.kotlinddb.exceptions.NotFoundException
import com.example.kotlinddb.models.data.Offering
import com.example.kotlinddb.repositories.OfferingRepository
import io.jooby.Context
import io.jooby.StatusCode
import io.jooby.exception.StatusCodeException

class RoasterOfferingsController(
    private val offeringsRepository: OfferingRepository
) {

    fun index(ctx: Context): List<Offering> {
        val roasterId = ctx.path("id").value()

        return try {
            offeringsRepository.findAllByRoaster(roasterId)
        } catch (e: Exception) {
            when (e) {
                is NotFoundException -> throw StatusCodeException(StatusCode.NOT_FOUND, e.message ?: "", e)
                else -> throw StatusCodeException(StatusCode.SERVER_ERROR, e.message ?: "", e)
            }
        }
    }
}
