plugins {
    kotlin("jvm").version(libs.versions.kotlin).apply(false)
}

subprojects {
    group = "org.gradle.experimental"
    version = "0.1.0-SNAPSHOT"
}
