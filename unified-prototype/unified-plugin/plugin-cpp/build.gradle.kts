plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
}

description = "Implements the declarative C++ DSL prototype"

dependencies {
    implementation(project(":plugin-common"))
}

gradlePlugin {
    plugins {
        create("cpp-library") {
            id = "org.gradle.experimental.cpp-library"
            implementationClass = "org.gradle.api.experimental.cpp.StandaloneCppLibraryPlugin"
            tags = setOf("declarative-gradle")
        }
        create("cpp-application") {
            id = "org.gradle.experimental.cpp-application"
            implementationClass = "org.gradle.api.experimental.cpp.StandaloneCppApplicationPlugin"
            tags = setOf("declarative-gradle")
        }
        create("swift-ecosystem") {
            id = "org.gradle.experimental.cpp-ecosystem"
            implementationClass = "org.gradle.api.experimental.cpp.CppEcosystemPlugin"
            tags = setOf("declarative-gradle")
        }
    }
}
