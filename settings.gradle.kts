@file:Suppress("UnstableApiUsage")

rootProject.name = "media-organizer"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("cli-app")
include("core")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
