package com.example.kotlinddb.config

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

class DynamoDBConfig {
    // https://forums.aws.amazon.com/thread.jspa?messageID=717048 #RAGE

    fun client(): DynamoDbClient {
        return DynamoDbClient.builder()
            .endpointOverride(URI.create("http://localhost:8000"))
            .credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create("key", "secret"))
            )
            .build()
    }
}
