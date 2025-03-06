import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    jacoco
    checkstyle
    application

    id("io.freefair.lombok") version "8.12.1"
    id("io.sentry.jvm.gradle") version "5.3.0"
    id("com.github.ben-manes.versions") version "0.52.0"

    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("hexlet.code.AppApplication")
}

buildscript {
    repositories {
        mavenCentral()
    }
}

sentry {
    includeSourceContext.set(true)

    org.set("demo-kq0")
    projectName.set("java-spring-boot")
    authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")

    implementation("net.datafaker:datafaker:2.4.2")
    implementation("org.instancio:instancio-junit:5.4.0")
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:4.1.0")

    testImplementation(platform("org.junit:junit-bom:5.12.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    implementation("me.paulschwarz:spring-dotenv:4.0.0")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs = listOf(
        "-javaagent:${classpath.find { it.name.contains("byte-buddy-agent") }?.absolutePath}"
    )
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        showStandardStreams = true
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

tasks.sentryBundleSourcesJava {
    enabled = System.getenv("SENTRY_AUTH_TOKEN") != null
}
