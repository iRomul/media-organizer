plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("io.github.iromul.media.CliKt")
}

dependencies {
    implementation("io.micrometer:micrometer-core:1.10.6")
    implementation(projects.musicLibrary.core)
    implementation(libs.kotlin.stdlib.jdk7)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.reflect)
    implementation(libs.clikt)
    implementation(libs.jaudiotagger)
    testImplementation(libs.junit.jupiter)
}
