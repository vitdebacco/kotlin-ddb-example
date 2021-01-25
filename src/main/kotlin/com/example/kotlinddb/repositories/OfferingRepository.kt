package com.example.kotlinddb.repositories

import com.example.kotlinddb.exceptions.NotFoundException
import com.example.kotlinddb.models.data.Offering
import com.example.kotlinddb.models.filters.OriginFilter
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.net.URL
import java.time.Instant

// https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Query.html
// https://aws.amazon.com/blogs/database/using-sort-keys-to-organize-data-in-amazon-dynamodb/
// https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/bp-sort-keys.html
class OfferingRepository(private val client: DynamoDbClient) {

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val TABLE_NAME = "Coffee"
        private const val INDEX_NAME = "GSI1"
        private const val PK = "ROASTER#"
        private const val SK = "OFFERING#"
        private const val LIMIT = 100
    }

    fun findAllByRoaster(roasterId: String): List<Offering> {
        val partitionKey = "$PK$roasterId"
        logger.debug("retrieving offerings for $partitionKey")

        val req = QueryRequest.builder().tableName(TABLE_NAME)
            .keyConditionExpression("PK = :partitionKey")
            .expressionAttributeValues(
                mapOf(
                    ":partitionKey" to AttributeValue.builder().s(partitionKey).build()
                )
            )
            .limit(LIMIT)
            .build()

        val resp = client.query(req)
        logger.debug("query returned ${resp.count()} items")

        val respItems = resp.items()
        if (respItems.isEmpty()) throw NotFoundException("roaster '$roasterId' not found")

        // pull the roaster from the response items map
        val roaster = transformRoaster(respItems.first())

        // only transform the offerings
        return respItems.subList(1, respItems.size).map { transformOffering(it, roaster) }
    }

    fun findAllByOrigin(originFilter: OriginFilter): List<Offering> {
        val origin = originFilter.originName

        val roasterIdParam = originFilter.roasterId ?: ""

        val gsi1PK = "ORIGIN#$origin"
        val gsi1SK = "ROASTER#$roasterIdParam"
        logger.debug("gsi1PK: $gsi1PK gsi1SK: $gsi1SK")

        val expressionAttrValues = mutableMapOf(":gsi1PK" to AttributeValue.builder().s(gsi1PK).build())
        val expression = if (roasterIdParam.isBlank()) {
            "GSI1PK = :gsi1PK"
        } else {
            expressionAttrValues[":gsi1SK"] = AttributeValue.builder().s(gsi1SK).build()
            "GSI1PK = :gsi1PK AND begins_with(GSI1SK, :gsi1SK)"
        }

        logger.debug("number of exprAttrValues keys: ${expressionAttrValues.keys.size}")

        val req = QueryRequest.builder().tableName(TABLE_NAME).indexName(INDEX_NAME)
            .keyConditionExpression(expression)
            .expressionAttributeValues(
                expressionAttrValues
            )
            .limit(LIMIT)
            .build()

        val resp = client.query(req)
        logger.debug("query returned ${resp.count()} items")

        val respItems = resp.items()
        if (respItems.isEmpty()) return emptyList()

        // pull the roaster ID from the sort key
        val sortKey = respItems.first()["GSI1SK"]?.s()!!.split("|").associate {
            val (left, right) = it.split("#")
            left.toLowerCase() to right
        }

        val roaster = mapOf("id" to sortKey["roaster"]!!)

        // only transform the offerings
        return respItems.map { transformOffering(it, roaster) }
    }

    private fun transformRoaster(it: Map<String, AttributeValue>): Map<String, String> {
        logger.debug("transforming roaster")

        return mapOf(
            "id" to it["Id"]!!.s(),
            "name" to it["Name"]!!.s(),
            "status" to it["Status"]!!.s(),
            "url" to it["Url"]!!.s(),
            "created_at" to it["CreatedAt"]!!.s()
        )
    }

    private fun transformOffering(
        it: Map<String, AttributeValue>,
        roaster: Map<String, String> = emptyMap()
    ): Offering {
        logger.debug("transforming offering")
        return Offering(
            id = it["Id"]!!.s(),
            name = it["Name"]!!.s(),
            status = it["Status"]!!.s(),
            url = URL(it["Url"]!!.s()),
            createdAt = Instant.parse(it["CreatedAt"]!!.s()),
            origin = it["Origin"]!!.s(),
            tastingNotes = it["TastingNotes"]!!.ss().toSet(),
            description = it["Description"]?.s() ?: "",
            price = it["Price"]?.s() ?: "",
            roaster = roaster,
            additionalDetails = it["AdditionalDetails"]?.m()?.mapValues {
                it.value.s()
            } ?: emptyMap()
        )
    }
}
