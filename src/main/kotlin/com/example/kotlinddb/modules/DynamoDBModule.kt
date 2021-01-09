package com.example.kotlinddb.modules

import io.jooby.Extension
import io.jooby.Jooby
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class DynamoDBModule(
    private val client: DynamoDbClient
) : Extension {

    override fun install(application: Jooby) {
        val registry = application.services

        registry.put(DynamoDbClient::class.java, client)
    }
}
