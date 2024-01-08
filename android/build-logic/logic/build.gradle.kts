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

dependencies {
    // Module dependency for com.android.application
    implementation("com.android.application:com.android.application.gradle.plugin:8.1.0")
    implementation("org.jetbrains.kotlin.android:org.jetbrains.kotlin.android.gradle.plugin:1.9.20")
}

gradlePlugin {
    plugins {
        create("playground-android") {
            id = "org.gradle.playground-android"
            implementationClass = "org.gradle.api.experimental.AndroidPlugin"
        }
    }
}