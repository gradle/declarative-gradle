plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
}

description = "Implements the declarative Swift DSL prototype"

dependencies {
    implementation(project(":plugin-common"))
}

gradlePlugin {
    plugins {
        create("swift-library") {
            id = "org.gradle.experimental.swift-library"
            implementationClass = "org.gradle.api.experimental.swift.StandaloneSwiftLibraryPlugin"
            tags = setOf("declarative-gradle")
        }
        create("swift-application") {
            id = "org.gradle.experimental.swift-application"
            implementationClass = "org.gradle.api.experimental.swift.StandaloneSwiftApplicationPlugin"
            tags = setOf("declarative-gradle")
        }
        create("swift-ecosystem") {
            id = "org.gradle.experimental.swift-ecosystem"
            implementationClass = "org.gradle.api.experimental.swift.SwiftEcosystemPlugin"
            tags = setOf("declarative-gradle")
        }
    }
}
