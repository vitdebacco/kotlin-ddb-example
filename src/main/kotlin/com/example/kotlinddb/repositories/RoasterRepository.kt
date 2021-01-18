package com.example.kotlinddb.repositories

import com.example.kotlinddb.models.api.RoasterCreateRequest
import com.example.kotlinddb.models.api.RoasterUpdateRequest
import com.example.kotlinddb.models.data.Roaster
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
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
                createdAt = Instant.parse(it["CreatedAt"]!!.s()),
                updatedAt = nullableDate(it["UpdatedAt"])
            )
        }
    }

    fun create(roasterCreateRequest: RoasterCreateRequest): Roaster {
        val roasterId = roasterCreateRequest.name.toLowerCase().replace(" ", "-")
        val partitionKey = "$PARTITION_KEY$roasterId"
        val sortKey = SORT_KEY

        val roaster = Roaster(
            id = roasterId,
            name = roasterCreateRequest.name,
            url = roasterCreateRequest.url,
            status = roasterCreateRequest.status,
            createdAt = Instant.now()
        )

        val request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(
                mapOf(
                    "PartitionKey" to AttributeValue.builder().s(partitionKey).build(),
                    "SortKey" to AttributeValue.builder().s(sortKey).build(),
                    "ItemType" to AttributeValue.builder().s(ITEM_TYPE).build(),
                    "Id" to AttributeValue.builder().s(roaster.id).build(),
                    "Name" to AttributeValue.builder().s(roaster.name).build(),
                    "Url" to AttributeValue.builder().s(roaster.url.toString()).build(),
                    "Status" to AttributeValue.builder().s(roaster.status).build(),
                    "CreatedAt" to AttributeValue.builder().s(roaster.createdAt.toString()).build()
                )
            )
            .conditionExpression("attribute_not_exists(PartitionKey)")
            .build()

        client.putItem(request)

        return roaster
    }

    fun update(id: String, updateRequest: RoasterUpdateRequest) {
        val partitionKey = "$PARTITION_KEY$id"
        val sortKey = SORT_KEY

        val updates = toAttributeValueUpdate(updateRequest)

        // Investigate using `conditionExpression("attribute_exists(PartitionKey)")` to validate the existence of the
        // item we are attempting to update. It seems this may not be possible along with `attributeUpdates`.
        val request = UpdateItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(
                mapOf(
                    "PartitionKey" to AttributeValue.builder().s(partitionKey).build(),
                    "SortKey" to AttributeValue.builder().s(sortKey).build()
                )
            )
            .attributeUpdates(updates)
            .build()

        client.updateItem(request)
    }

    fun delete(id: String) {
        val partitionKey = "$PARTITION_KEY$id"
        val sortKey = SORT_KEY

        val request = DeleteItemRequest.builder()
            .tableName(TABLE_NAME)
            .key(
                mapOf(
                    "PartitionKey" to AttributeValue.builder().s(partitionKey).build(),
                    "SortKey" to AttributeValue.builder().s(sortKey).build()
                )
            )
            .conditionExpression("attribute_exists(PartitionKey)")
            .build()

        client.deleteItem(request)
    }

    private fun nullableDate(attributeValue: AttributeValue?): Instant? {
        val s = attributeValue?.s() ?: return null
        return Instant.parse(s)
    }

    private fun toAttributeValueUpdate(updateRequest: RoasterUpdateRequest): Map<String, AttributeValueUpdate> {
        val updates = mutableMapOf<String, AttributeValueUpdate>()

        if (updateRequest.name != null) {
            updates["Name"] =
                AttributeValueUpdate.builder().value(AttributeValue.builder().s(updateRequest.name).build()).build()
        }

        if (updateRequest.url != null) {
            updates["Url"] = AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(updateRequest.url.toString()).build()).build()
        }

        if (updateRequest.status != null) {
            updates["Status"] = AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(updateRequest.status).build()).build()
        }

        updates["UpdatedAt"] = AttributeValueUpdate.builder()
            .value(AttributeValue.builder().s(Instant.now().toString()).build()).build()

        return updates
    }
}
