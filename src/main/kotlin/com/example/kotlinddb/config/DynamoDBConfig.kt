package com.example.kotlinddb.config

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

class DynamoDBConfig {

    // https://forums.aws.amazon.com/thread.jspa?messageID=717048 #RAGE

    private val client: DynamoDbClient = DynamoDbClient.builder()
        .endpointOverride(URI.create("http://localhost:8000"))
//        .region(Region.of("ddblocal"))
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create("key", "secret"))
        )
            //AWSStaticCredentialsProvider(BasicAWSCredentials("key", "secret"))
//        .credentialsProvider(DefaultCredentialsProvider.builder().build())
        .build()

//    fun enhancedClient(): DynamoDbEnhancedClient {
//        return DynamoDbEnhancedClient.builder()
//            .dynamoDbClient(client()).build()
//    }

    fun client(): DynamoDbClient {
        return client
    }

}