@file:Suppress("UnstableApiUsage")

plugins {
    id("org.gradle.playground-kmp")
}

repositories {
    mavenCentral()
    google()
}

kmpApplication {
    languageVersion = "1.9"
    publishSources = true

    // Common dependencies
    dependencies {
        implementation(libs.kotlinx.datetime)
    }

    targets {
        jvm {
            // Dependencies used only by the JVM target
            dependencies {
                implementation(libs.kotlinx.coroutines)
            }
        }
    }
}

publishing {
    repositories {
        maven {
            name = "test"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}