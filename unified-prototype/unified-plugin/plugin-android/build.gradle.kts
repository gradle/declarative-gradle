plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
}

buildscript {
    dependencies {
        constraints {
            classpath("com.google.dagger:dagger:2.50")
        }
    }
}

description = "Implements the declarative Android DSL prototype"

dependencies {
    api(project(":plugin-common"))
    api(libs.android.agp.application)
    api(libs.android.kotlin.android)
    api("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.21-1.0.16")
    api("com.google.dagger:hilt-android-gradle-plugin:2.50")
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
