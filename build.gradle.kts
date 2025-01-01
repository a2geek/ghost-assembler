plugins {
    application
    id("org.springframework.boot") version "3.4.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    implementation("org.jcommander:jcommander:1.85")

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "com.webcodepro.asm.Main"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
