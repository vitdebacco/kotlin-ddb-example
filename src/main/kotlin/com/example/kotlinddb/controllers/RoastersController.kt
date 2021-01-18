package com.example.kotlinddb.controllers

import com.example.kotlinddb.extensions.noChangesRequested
import com.example.kotlinddb.models.api.RoasterCreateRequest
import com.example.kotlinddb.models.api.RoasterUpdateRequest
import com.example.kotlinddb.models.data.Roaster
import com.example.kotlinddb.repositories.RoasterRepository
import io.jooby.Context
import io.jooby.StatusCode
import io.jooby.exception.StatusCodeException
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
import software.amazon.awssdk.services.dynamodb.model.RequestLimitExceededException
import java.time.Instant

class RoastersController(
    private val roasterRepository: RoasterRepository
) {

    fun show(ctx: Context): Roaster {
        val id = ctx.path("id").value()

        return roasterRepository.findById(id)
            ?: throw StatusCodeException(StatusCode.NOT_FOUND, "roaster '$id' not found")
    }

    fun create(ctx: Context): Roaster {
        val roasterCreateRequest = ctx.body(RoasterCreateRequest::class.java)

        return try {
            val createdRoaster = roasterRepository.create(roasterCreateRequest)
            ctx.responseCode = StatusCode.CREATED

            createdRoaster
        } catch (e: ConditionalCheckFailedException) {
            throw StatusCodeException(StatusCode.CONFLICT, "roaster '${roasterCreateRequest.name}' already exists")
        } catch (e: RequestLimitExceededException) {
            throw StatusCodeException(StatusCode.TOO_MANY_REQUESTS, e.message ?: "too many requests")
        } catch (e: Exception) {
            throw StatusCodeException(StatusCode.SERVER_ERROR, e.message ?: "unknown exception occurred")
        }
    }

    fun update(ctx: Context): Roaster {
        val id = ctx.path("id").value()
        val roasterUpdateRequest = ctx.body(RoasterUpdateRequest::class.java)

        if (roasterUpdateRequest.noChangesRequested())
            throw StatusCodeException(StatusCode.NOT_MODIFIED, "no changes requested for roaster: $id")

        return try {
            roasterRepository.update(id, roasterUpdateRequest)

            roasterRepository.findById(id) ?: throw StatusCodeException(StatusCode.NOT_FOUND, "roaster '$id' not found")
        } catch (e: ConditionalCheckFailedException) {
            throw StatusCodeException(StatusCode.NOT_FOUND, "roaster '$id' not found")
        } catch (e: RequestLimitExceededException) {
            throw StatusCodeException(StatusCode.TOO_MANY_REQUESTS, e.message ?: "too many requests")
        } catch (e: StatusCodeException) {
            throw e
        } catch (e: Exception) {
            throw StatusCodeException(StatusCode.SERVER_ERROR, e.message ?: "unknown exception occurred")
        }
    }

    fun delete(ctx: Context): Roaster {
        val id = ctx.path("id").value()

        return try {
            val roaster = roasterRepository.findById(id)
                ?: throw StatusCodeException(StatusCode.NOT_FOUND, "roaster '$id' not found")

            roasterRepository.delete(id)
            roaster.deletedAt = Instant.now()

            roaster
        } catch (e: ConditionalCheckFailedException) {
            throw StatusCodeException(StatusCode.NOT_FOUND, "roaster not found: $id")
        } catch (e: RequestLimitExceededException) {
            throw StatusCodeException(StatusCode.TOO_MANY_REQUESTS, e.message ?: "too many requests")
        } catch (e: StatusCodeException) {
            throw e
        } catch (e: Exception) {
            throw StatusCodeException(StatusCode.SERVER_ERROR, e.message ?: "unknown exception occurred")
        }
    }
}
