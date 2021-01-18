package com.example.kotlinddb

import com.example.kotlinddb.config.DynamoDBConfig
import com.example.kotlinddb.config.JacksonConfig
import com.example.kotlinddb.controllers.OfferingsController
import com.example.kotlinddb.controllers.RoasterOfferingsController
import com.example.kotlinddb.controllers.RoastersController
import com.example.kotlinddb.modules.DynamoDBModule
import com.example.kotlinddb.repositories.OfferingRepository
import com.example.kotlinddb.repositories.RoasterRepository
import io.jooby.Kooby
import io.jooby.json.JacksonModule
import io.jooby.runApp
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class App : Kooby({

    val logger = LoggerFactory.getLogger(javaClass)

    install(JacksonModule(JacksonConfig().objectMapper()))
    install(DynamoDBModule(DynamoDBConfig().client()))

    val dynamoDBClient = require(DynamoDbClient::class.java)

    val roasterRepository = RoasterRepository(client = dynamoDBClient)
    val offeringsRepository = OfferingRepository(client = dynamoDBClient)

    val roastersController = RoastersController(roasterRepository)
    val roasterOfferingsController = RoasterOfferingsController(offeringsRepository)
    val offeringsController = OfferingsController(offeringsRepository)

    coroutine {
        get("/health_check") { AppStatus() }
        get("/roasters/{id}") { roastersController.show(ctx) }
        post("/roasters") { roastersController.create(ctx) }
        put("/roasters/{id}") { roastersController.update(ctx) }
        delete("/roasters/{id}") { roastersController.delete(ctx) }
        get("/roasters/{id}/offerings") { roasterOfferingsController.index(ctx) }
        get("/offerings") { offeringsController.index(ctx) }
    }
})

fun main(args: Array<String>) {
    runApp(args, App::class)
}

data class AppStatus(val app: String = "up")
