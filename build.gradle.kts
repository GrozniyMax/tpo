plugins {
    kotlin("jvm") version "2.1.21"
    jacoco
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
}

group = "tpo.maxim"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")

    // Selenium WebDriver
    implementation("org.seleniumhq.selenium:selenium-java:4.28.1")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.28.1")

    // WebDriverManager для автоматической загрузки драйверов
    testImplementation("io.github.bonigarcia:webdrivermanager:5.9.2")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("webdriver.chrome.driver", System.getenv("CHROME_DRIVER_PATH") ?: "chromedriver")
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*\$\$inlined\$*",
                    "*\$lambda\$*",
                )
                annotatedBy(
                    "*Generated*",
                )
            }
        }

        verify {
            rule {
                minBound(25)
            }
        }
    }
}

tasks.register("reports") {
    group = "reporting"
    description = "Открывает отчёты в браузере"

    dependsOn(tasks.test, tasks.named("koverHtmlReport"))

    doLast {
        exec { commandLine("open", "build/reports/tests/test/index.html") }
        exec { commandLine("open", "build/reports/kover/html/index.html") }
    }
}




kotlin {
    jvmToolchain(21)
}
