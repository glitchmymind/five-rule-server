plugins {
    application
    kotlin("jvm") version "1.7.10"
//    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
    kotlin("plugin.serialization") version "1.7.10"
}

group = "com.fiverules"
version = "0.0.1"
application {
    mainClass.set("com.fiverules.ApplicationKt")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    // Connection
    implementation(deps.ktor.server.core)
    implementation(deps.ktor.gson)
    implementation(deps.ktor.serialization)
    implementation(deps.ktor.server.netty)
    implementation(deps.ktor.locations)
    implementation(deps.logback)

    // DB
    implementation(deps.exposed.core)
    implementation(deps.exposed.dao)
    implementation(deps.exposed.jdbc)

    implementation(deps.mysql.connector)

    implementation(deps.postgresql)
//    docker run --name exposed-db -p 5432:5432 -e POSTGRES_USER=exposed -e POSTGRES_PASSWORD=exposed -d postgres
//    implementation("org.flywaydb:flyway-core:8.5.0") // db migration

    implementation(deps.hikari)
//
//    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
//    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")

}

tasks {
    create("stage").dependsOn("installDist")
}