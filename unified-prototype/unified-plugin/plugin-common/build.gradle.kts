plugins {
    kotlin("jvm") version libs.versions.kotlin
}

dependencies {
    implementation(gradleApi())
}

description = "Common APIs and implementation classes shared by the Android, JVM, and KMP declarative prototypes"
