plugins {
    `kotlin-dsl`
}

description = "Implements the declarative Android DSL prototype"

dependencies {
    api(project(":plugin-common"))
    api(libs.android.agp.application)
    api(libs.android.kotlin.android)
}

gradlePlugin {
    plugins {
        create("android-library-plugin") {
            id = "org.gradle.experimental.android-library"
            implementationClass = "org.gradle.api.experimental.android.library.StandaloneAndroidLibraryPlugin"
        }
        create("android-application-plugin") {
            id = "org.gradle.experimental.android-application"
            implementationClass = "org.gradle.api.experimental.android.application.StandaloneAndroidApplicationPlugin"
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