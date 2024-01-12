plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("playground-kmp") {
            id = "org.gradle.playground-kmp"
            implementationClass = "org.gradle.api.experimental.KMPPlugin"
        }
    }
}