@file:Suppress("UnstableApiUsage")

rootProject.name = "media-organizer"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("cli-app")
include("core")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            version("kotlin", "1.7.0")
            version("ktor", "2.0.2")

            library("assertk-jvm", "com.willowtreeapps.assertk:assertk-jvm:0.22")
            library("clikt", "com.github.ajalt.clikt:clikt:3.5.0")
            library("jackson-databind", "com.fasterxml.jackson.core:jackson-databind:2.13.3")
            library("jackson-module-kotlin", "com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
            library("jaudiotagger", "org.jaudiotagger:jaudiotagger:2.0.1")
            library("junit-jupiter", "org.junit.jupiter:junit-jupiter:5.8.2")
            library("kotlin-logging", "io.github.microutils:kotlin-logging:2.1.23")
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")
            library("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib").versionRef("kotlin")
            library("kotlin-stdlib-jdk7", "org.jetbrains.kotlin", "kotlin-stdlib-jdk7").versionRef("kotlin")
            library("kotlin-stdlib-jdk8", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8").versionRef("kotlin")
            library("ktor-client-apache", "io.ktor", "ktor-client-apache").versionRef("ktor")
            library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation").versionRef("ktor")
            library("ktor-serialization-jackson", "io.ktor", "ktor-serialization-jackson").versionRef("ktor")
            library("mockk", "io.mockk:mockk:1.10.0")
            library("slf4j-simple", "org.slf4j:slf4j-simple:1.7.36")
            library("thumbnailator", "net.coobird:thumbnailator:[0.4, 0.5)")
        }
    }
}