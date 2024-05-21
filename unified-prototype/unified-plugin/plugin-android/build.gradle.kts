plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
}

description = "Implements the declarative Android DSL prototype"

dependencies {
    api(project(":plugin-common"))
    api(libs.android.agp.application)
    api(libs.android.kotlin.android)

    implementation(libs.ksp.plugin)
    implementation(libs.hilt.android.plugin)
    implementation(libs.kotlin.serialization.plugin)
    implementation(libs.room.plugin)

    implementation(libs.apache.commons.lang)
}

gradlePlugin {
    plugins {
        create("android-library") {
            id = "org.gradle.experimental.android-library"
            displayName = "Android Library Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Android libraries"
            implementationClass = "org.gradle.api.experimental.android.library.StandaloneAndroidLibraryPlugin"
            tags = setOf("declarative-gradle", "android")
        }
        create("android-application") {
            id = "org.gradle.experimental.android-application"
            displayName = "Android Application Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Android applications"
            implementationClass = "org.gradle.api.experimental.android.application.StandaloneAndroidApplicationPlugin"
            tags = setOf("declarative-gradle", "android")
        }
        create("android-ecosystem") {
            id = "org.gradle.experimental.android-ecosystem"
            displayName = "Android Ecosystem Experimental Declarative Plugin"
            description = "Experimental declarative plugin for the Android ecosystem"
            implementationClass = "org.gradle.api.experimental.android.AndroidEcosystemPlugin"
            tags = setOf("declarative-gradle", "android")
        }
    }
}

// Compile against Java 17 since Android requires Java 17 at minimum
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
