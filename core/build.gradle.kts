plugins {
    java
    kotlin("jvm")
}

dependencies {
    implementation(libs.kotlin.stdlib.jdk7)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jaudiotagger)
    implementation(libs.kotlin.logging)
    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.slf4j.simple)
    implementation(libs.thumbnailator)
    testImplementation(libs.assertk.jvm)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
}
