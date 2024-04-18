plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
}

description = "Implements the declarative Android DSL prototype"

dependencies {
    api(project(":plugin-common"))
    api(libs.android.agp.application)
    api(libs.android.kotlin.android)
}

gradlePlugin {
    plugins {
        create("android-library") {
            id = "org.gradle.experimental.android-library"
            implementationClass = "org.gradle.api.experimental.android.library.StandaloneAndroidLibraryPlugin"
            tags = setOf("declarative-gradle", "android")
        }
        create("android-application") {
            id = "org.gradle.experimental.android-application"
            implementationClass = "org.gradle.api.experimental.android.application.StandaloneAndroidApplicationPlugin"
            tags = setOf("declarative-gradle", "android")
        }
        create("android-ecosystem") {
            id = "org.gradle.experimental.android-ecosystem"
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
