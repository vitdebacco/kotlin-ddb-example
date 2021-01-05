package com.example.kotlinddb.repositories

import com.example.kotlinddb.models.data.Roaster
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
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
    private val client: DynamoDbClient
) {

    companion object {
        private const val TABLE_NAME = "Coffee"
        private const val PARTITION_KEY = "ROASTER#"
        private const val SORT_KEY = "#INFO"
        private const val ITEM_TYPE = "Roaster"
    }

    fun findById(id: String): Roaster? {
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

        val r = client.getItem(request)
        if (!r.hasItem()) return null

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
}
