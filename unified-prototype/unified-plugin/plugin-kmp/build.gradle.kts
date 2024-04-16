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
            implementationClass = "org.gradle.api.experimental.kmp.StandaloneKmpLibraryPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
        create("kotlin-library") {
            id = "org.gradle.experimental.kotlin-jvm-library"
            implementationClass = "org.gradle.api.experimental.kotlin.StandaloneKotlinJvmLibraryPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
        create("kotlin-application") {
            id = "org.gradle.experimental.kotlin-jvm-application"
            implementationClass = "org.gradle.api.experimental.kotlin.StandaloneKotlinJvmApplicationPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
        create("kmp-ecosystem") {
            id = "org.gradle.experimental.kmp-ecosystem"
            implementationClass = "org.gradle.api.experimental.kmp.KmpEcosystemPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
    }
}
