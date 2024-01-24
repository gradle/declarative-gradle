plugins {
    `kotlin-dsl`
}

description = "Implements the declarative JVM DSL prototype"

dependencies {
    implementation(project(":plugin-common"))
}

gradlePlugin {
    plugins {
        create("jvm-plugin") {
            id = "org.gradle.experimental.jvm-library"
            implementationClass = "org.gradle.api.experimental.jvm.StandaloneJvmLibraryPlugin"
        }
    }
}