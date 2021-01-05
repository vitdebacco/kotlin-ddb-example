package com.example.kotlinddb.controllers

import com.example.kotlinddb.models.data.Offering
import com.example.kotlinddb.models.filters.OriginFilter
import com.example.kotlinddb.repositories.OfferingRepository
import io.jooby.Context
import io.jooby.StatusCode
import io.jooby.exception.StatusCodeException

class OfferingsController(
    private val offeringRepository: OfferingRepository
) {

    fun index(ctx: Context): List<Offering> {
        val queryMap = ctx.queryMap()

        val filter = OriginFilter(
            originName = queryMap["origin_name"]
                ?: throw StatusCodeException(StatusCode.BAD_REQUEST, "param 'origin_name' is required"),
            roasterId = queryMap["roaster_id"]
        )

        return offeringRepository.findAllByOrigin(filter)
    }
}
