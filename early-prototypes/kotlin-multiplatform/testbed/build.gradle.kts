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
        implementation(libs.kotlin.test)
        implementation(libs.kotlinx.datetime)
    }

    targets {
        val jvm by creating {
            dependencies {
                implementation(libs.kotlinx.coroutines)
            }
        }

        val js by creating
    }
}

// This is for testing the publishSources property, ideally this block would live inside the kmpApplication block
publishing {
    repositories {
        maven {
            name = "test"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}