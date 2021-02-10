plugins {
    java
    kotlin("jvm") version "1.4.30"
}

group = "io.github.iromul.media"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

subprojects {
    tasks.test {
        useJUnitPlatform()
    }

    tasks.compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    tasks.compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}