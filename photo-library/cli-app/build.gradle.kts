plugins {
    java
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.clikt)
    implementation("com.h2database:h2:2.3.232")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    implementation("org.apache.commons:commons-imaging:1.0.0-alpha5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
