plugins {
    kotlin("jvm") version "2.1.21"
    jacoco
}

group = "tpo.maxim"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport") // Генерация отчёта после выполнения тестов
}

tasks.jacocoTestReport {
    reports {
        html.required.set(true) // Людям удобнее читать HTML
    }
}

kotlin {
    jvmToolchain(21)
}