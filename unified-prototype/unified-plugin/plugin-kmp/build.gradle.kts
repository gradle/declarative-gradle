plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
}

description = "Implements the declarative KMP DSL prototype"

dependencies {
    implementation(project(":plugin-common"))
    implementation(project(":plugin-jvm"))
    implementation(libs.kotlin.multiplatform)
    implementation(libs.kotlin.jvm)
}

gradlePlugin {
    plugins {
        create("kmp-library") {
            id = "org.gradle.experimental.kmp-library"
            displayName = "KMP Library Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Kotlin Multiplatform libraries"
            implementationClass = "org.gradle.api.experimental.kmp.StandaloneKmpLibraryPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
        create("kotlin-library") {
            id = "org.gradle.experimental.kotlin-jvm-library"
            displayName = "Kotlin JVM Library Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Kotlin JVM libraries"
            implementationClass = "org.gradle.api.experimental.kotlin.StandaloneKotlinJvmLibraryPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
        create("kotlin-application") {
            id = "org.gradle.experimental.kotlin-jvm-application"
            displayName = "Kotlin JVM Application Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Kotlin JVM applications"
            implementationClass = "org.gradle.api.experimental.kotlin.StandaloneKotlinJvmApplicationPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
        create("kmp-ecosystem") {
            id = "org.gradle.experimental.kmp-ecosystem"
            displayName = "KMP Ecosystem Experimental Declarative Plugin"
            description = "Experimental declarative plugin for the Kotlin Multiplatform ecosystem"
            implementationClass = "org.gradle.api.experimental.kmp.KmpEcosystemPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
    }
}
