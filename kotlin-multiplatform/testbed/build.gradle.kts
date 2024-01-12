@file:Suppress("UnstableApiUsage")

plugins {
    id("org.gradle.playground-kmp")
}

repositories {
    mavenCentral()
    google()
}

defaultTasks("build")

kmpApplication {
    dependencies {
        implementation(libs.kotlinx.coroutines)
    }

    sourceSets {
        commonMain {
        }

        jsMain {

        }

        jvmMain {
        }
    }
}
