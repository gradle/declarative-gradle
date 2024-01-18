@file:Suppress("UnstableApiUsage")

plugins {
    id("org.gradle.playground-kmp")
}

repositories {
    mavenCentral()
    google()
}

kmpApplication {
    platforms = listOf("jvm", "js")

    languageVersion = "1.9"
    publishSources = true

    // Common dependencies
    dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotlinx.datetime)
    }

    targets {
        jvm {
            dependencies {
                implementation(libs.kotlinx.coroutines)
            }
        }
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
