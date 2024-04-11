plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
}

description = "Implements the declarative KMP DSL prototype"

dependencies {
    implementation(project(":plugin-common"))
    implementation(libs.kotlin.multiplatform)
}

gradlePlugin {
    plugins {
        create("kmp-plugin") {
            id = "org.gradle.experimental.kmp-library"
            implementationClass = "org.gradle.api.experimental.kmp.StandaloneKmpLibraryPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
        create("kmp-ecosystem") {
            id = "org.gradle.experimental.kmp-ecosystem"
            implementationClass = "org.gradle.api.experimental.kmp.KmpEcosystemPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
    }
}
