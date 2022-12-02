plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("io.github.iromul.media.CliKt")
}

dependencies {
    implementation(projects.core)
    implementation(libs.kotlin.stdlib.jdk7)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.clikt)
    implementation(libs.jaudiotagger)
    testImplementation(libs.junit.jupiter)
}
