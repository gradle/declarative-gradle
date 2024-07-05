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
            displayName = "C++ Library Experimental Declarative Plugin"
            description = "Experimental declarative plugin for C++ libraries"
            implementationClass = "org.gradle.api.experimental.cpp.StandaloneCppLibraryPlugin"
            tags = setOf("declarative-gradle")
        }
        create("cpp-application") {
            id = "org.gradle.experimental.cpp-application"
            displayName = "C++ Application Experimental Declarative Plugin"
            description = "Experimental declarative plugin for C++ applications"
            implementationClass = "org.gradle.api.experimental.cpp.StandaloneCppApplicationPlugin"
            tags = setOf("declarative-gradle")
        }
        create("cpp-ecosystem") {
            id = "org.gradle.experimental.cpp-ecosystem"
            displayName = "C++ Ecosystem Experimental Declarative Plugin"
            description = "Experimental declarative plugin for the C++ ecosystem"
            implementationClass = "org.gradle.api.experimental.cpp.CppEcosystemPlugin"
            tags = setOf("declarative-gradle")
        }
    }
}
