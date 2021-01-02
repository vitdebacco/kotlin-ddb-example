package com.example.kotlinddb.repositories

import com.example.kotlinddb.models.data.Roaster
import io.jooby.StatusCode
import io.jooby.exception.StatusCodeException
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.net.URL
import java.time.Instant


/*
    Helpful links
    https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Programming.html

    Sample Data Initialization
    https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/CodeSamples.html

    https://awscli.amazonaws.com/v2/documentation/api/latest/reference/dynamodb/put-item.html
 */

class RoasterRepository(
    private val dynamoDBClient: DynamoDbClient
) {

    companion object {
        private const val TABLE_NAME = "Coffee"
        private const val PARTITION_KEY = "ROASTER#"
        private const val SORT_KEY = "#INFO"
        private const val ITEM_TYPE = "Roaster"
    }

    fun findById(id: String): Roaster {
        val partitionKey = "$PARTITION_KEY$id"
        val sortKey = SORT_KEY

        val request = GetItemRequest.builder()
            .key(
                mapOf(
                    "PartitionKey" to AttributeValue.builder().s(partitionKey).build(),
                    "SortKey" to AttributeValue.builder().s(sortKey).build()
                )
            )
            .tableName(TABLE_NAME)
            .build()

        val r = dynamoDBClient.getItem(request)
        if (!r.hasItem()) throw StatusCodeException(StatusCode.NOT_FOUND, "roaster '$id' not found")

        return r.item().let {
            Roaster(
                id = it["Id"]!!.s(),
                name = it["Name"]!!.s(),
                status = it["Status"]!!.s(),
                url = URL(it["Url"]!!.s()),
                createdAt = Instant.parse(it["CreatedAt"]!!.s())
            )
        }
    }

    fun create() {
        val roasterName = "Counter Culture Coffee"
        val roasterId = roasterName.toLowerCase().replace(" ", "-")
        val roasterUrl = "https://counterculturecoffee.com/"

        val partitionKey = "$PARTITION_KEY$roasterId"
        val sortKey = SORT_KEY

        val request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(
                mapOf(
                    "PartitionKey" to AttributeValue.builder().s(partitionKey).build(),
                    "SortKey" to AttributeValue.builder().s(sortKey).build(),
                    "ItemType" to AttributeValue.builder().s(ITEM_TYPE).build(),
                    "Id" to AttributeValue.builder().s(roasterId).build(),
                    "Name" to AttributeValue.builder().s(roasterName).build(),
                    "Url" to AttributeValue.builder().s(roasterUrl).build(),
                    "Status" to AttributeValue.builder().s("active").build(),
                    "CreatedAt" to AttributeValue.builder().s(Instant.now().toString()).build()
                )
            )
            .build()

        val result = dynamoDBClient.putItem(request)
    }

}