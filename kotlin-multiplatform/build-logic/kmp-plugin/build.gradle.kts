plugins {
    `kotlin-dsl`
    //alias(libs.plugins.kotlinMultiplatform).apply(false)
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

dependencies {
    implementation(libs.kotlin.multiplatform)
    implementation(libs.guava)
}

tasks.named("processResources", Copy::class) {
    from("../gradle/libs.versions.toml") {
        into("META-INF/catalogs")
    }
}

gradlePlugin {
    plugins {
        create("playground-kmp") {
            id = "org.gradle.playground-kmp"
            implementationClass = "org.gradle.api.experimental.KMPPlugin"
        }
    }
}