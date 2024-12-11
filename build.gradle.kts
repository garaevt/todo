plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.serialization") version "1.9.25"
    id("io.qameta.allure") version "2.10.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val restAssuredVersion = "5.3.0"
val assertJVersion = "3.23.1"
val log4jVersion = "2.20.0"
val junitVersion = "5.9.3"
val jacksonVersion = "2.15.2"
val allureVersion = "2.29.1"
val okhttpVersion = "4.11.0"
val mockWebServerVersion = "4.11.0"
val awaitilityVersion = "4.2.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("io.rest-assured:rest-assured:$restAssuredVersion")
    implementation("io.rest-assured:json-path:$restAssuredVersion")
    implementation("io.rest-assured:json-schema-validator:$restAssuredVersion")

    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")

    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")

    testImplementation("org.assertj:assertj-core:$assertJVersion")

    testImplementation("io.qameta.allure:allure-junit5:$allureVersion")

    testImplementation("org.awaitility:awaitility:$awaitilityVersion")
}

allure {
    version = "2.20.1"
    autoconfigure = true
    aspectjweaver = true
}

tasks.test {
    useJUnitPlatform()

    maxParallelForks = Runtime.getRuntime().availableProcessors().div(2).takeIf { it > 0 } ?: 1

    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
    systemProperty("junit.jupiter.execution.parallel.mode.classes.default", "concurrent")

    testLogging {
        events("passed", "skipped", "failed")
    }

    systemProperty("env", System.getProperty("env") ?: "local")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "19"
}

tasks.register<Test>("testLocal") {
    systemProperty("env", "local")
    useJUnitPlatform()
}

tasks.register<Test>("testQA") {
    systemProperty("env", "qa")
    useJUnitPlatform()
}

tasks.register<Test>("testDev") {
    systemProperty("env", "dev")
    useJUnitPlatform()
}

tasks.register<Test>("testProd") {
    systemProperty("env", "prod")
    useJUnitPlatform()
}