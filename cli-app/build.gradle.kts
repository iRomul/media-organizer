plugins {
    kotlin("jvm")
    application
}

group = "io.github.iromul.media"
version = "1.0.0-SNAPSHOT"

application {
    mainClass.set("io.github.iromul.media.CliKt")
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.ajalt:clikt:2.3.0")
    implementation(project(":core"))
    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
