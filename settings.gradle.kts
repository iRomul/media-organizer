@file:Suppress("UnstableApiUsage")

rootProject.name = "media-organizer"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":music-library:cli-app")
include(":music-library:core")
include(":photo-library:cli-app")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
