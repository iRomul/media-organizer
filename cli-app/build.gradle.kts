plugins {
    java
    kotlin("jvm")
    application
}

group = "io.github.iromul.media"
version = "1.0.0-SNAPSHOT"

application {
    mainClassName = "io.github.iromul.media.CliKt"
}

repositories {
    jcenter()
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://dl.bintray.com/kotlin/kotlinx")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.ajalt:clikt:2.3.0")
    implementation(project(":core"))
}
