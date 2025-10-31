plugins {
    kotlin("jvm") version "2.2.10"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

application {
    mainClass.set("MainKt")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.squareup.okio:okio:3.15.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}