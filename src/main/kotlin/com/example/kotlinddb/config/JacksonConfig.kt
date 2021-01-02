package com.example.kotlinddb.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

class JacksonConfig {
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            this.propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
            this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            this.registerModule(JavaTimeModule()) // for JSR310
            this.registerModule(
                KotlinModule(
                    nullToEmptyCollection = true,
                    nullToEmptyMap = true,
                    nullIsSameAsDefault = true
                )
            )
        }
    }
}