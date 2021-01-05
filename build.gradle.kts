import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val awsDynamoSdkVersion: String by project
val jacksonVersion: String by project
val joobyVersion: String by project
val jUnitVersion: String by project
val kotlinVersion: String by project
val restAssuredVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.4.10"
    id("io.jooby.run") version "2.9.4"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.jmailen.kotlinter") version "3.3.0"
}

group = "me.vitdebacco"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClassName = "com.example.kotlinddb.AppKt"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("io.jooby:jooby-netty:$joobyVersion")
    implementation("io.jooby:jooby-jackson:$joobyVersion")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    // https://medium.com/better-programming/aws-java-sdk-v2-dynamodb-enhanced-client-with-kotlin-spring-boot-application-f880c74193a2
//    implementation("software.amazon.awssdk:dynamodb-enhanced:$awsDynamoSdkVersion")
    implementation("software.amazon.awssdk:dynamodb:$awsDynamoSdkVersion")

    // Jackson modules for serialization & deserialization
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    // The JSR310 dependency is required to properly serializing and deserializing Java 8 date & time objects
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("io.rest-assured:kotlin-extensions:$restAssuredVersion")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testImplementation("io.jooby:jooby-test:$joobyVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()

    // Show results for tests. Default behavior only reports failures.
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}