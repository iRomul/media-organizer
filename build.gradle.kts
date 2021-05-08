import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.32" apply false
}

group = "io.github.iromul.media"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_15
}

subprojects {
    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "15"
            useIR = true
        }
    }
}