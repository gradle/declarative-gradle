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
            displayName = "Swift Library Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Swift libraries"
            implementationClass = "org.gradle.api.experimental.swift.StandaloneSwiftLibraryPlugin"
            tags = setOf("declarative-gradle", "swift")
        }
        create("swift-application") {
            id = "org.gradle.experimental.swift-application"
            displayName = "Swift Application Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Swift applications"
            implementationClass = "org.gradle.api.experimental.swift.StandaloneSwiftApplicationPlugin"
            tags = setOf("declarative-gradle", "swift")
        }
        create("swift-ecosystem") {
            id = "org.gradle.experimental.swift-ecosystem"
            displayName = "Swift Ecosystem Experimental Declarative Plugin"
            description = "Experimental declarative plugin for the Swift ecosystem"
            implementationClass = "org.gradle.api.experimental.swift.SwiftEcosystemPlugin"
            tags = setOf("declarative-gradle", "swift")
        }
    }
}
