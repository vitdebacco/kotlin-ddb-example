package com.example.kotlinddb

import com.example.kotlinddb.config.DynamoDBConfig
import com.example.kotlinddb.config.JacksonConfig
import com.example.kotlinddb.repositories.OfferingRepository
import com.example.kotlinddb.repositories.RoasterRepository
import io.jooby.Kooby
import io.jooby.StatusCode
import io.jooby.exception.StatusCodeException
import io.jooby.json.JacksonModule
import io.jooby.runApp
import org.slf4j.LoggerFactory

class App : Kooby({

    val logger = LoggerFactory.getLogger(javaClass)

    install(JacksonModule(JacksonConfig().objectMapper()))

    logger.debug("initializing DynamoDB client")
    val dynamoDBClient = DynamoDBConfig().client()
    logger.debug("DynamoDB tables: ${dynamoDBClient.listTables().tableNames()}")
    logger.debug("DynamoDB client initialized")

    val roasterRepository = RoasterRepository(dynamoDBClient = dynamoDBClient)
    val offeringsRepository = OfferingRepository(client = dynamoDBClient)

    coroutine {
        get("/") { "Welcome to Jooby!" }
        get("/health_check") { AppStatus() }
        get("/roasters/{id}") {
            val roasterId = ctx.path("id").value()

            roasterRepository.findById(roasterId)
        }
        get("/roasters/{id}/offerings") {
            val roasterId = ctx.path("id").value()

            offeringsRepository.findAllByRoaster(roasterId)
        }
        get("/offerings") {
            offeringsRepository.findAllByOrigin(ctx.queryMap())
        }
    }

})

fun main(args: Array<String>) {
    runApp(args, App::class)
}

data class AppStatus(val app: String = "UP")
