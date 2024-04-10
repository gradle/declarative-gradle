plugins {
    `kotlin-dsl`
}

description = "Implements the declarative JVM DSL prototype"

dependencies {
    implementation(project(":plugin-common"))
}

gradlePlugin {
    plugins {
        create("jvm-library") {
            id = "org.gradle.experimental.jvm-library"
            implementationClass = "org.gradle.api.experimental.jvm.StandaloneJvmLibraryPlugin"
        }
        create("java-library") {
            id = "org.gradle.experimental.java-library"
            implementationClass = "org.gradle.api.experimental.java.StandaloneJavaLibraryPlugin"
        }
        create("java-application") {
            id = "org.gradle.experimental.java-application"
            implementationClass = "org.gradle.api.experimental.java.StandaloneJavaApplicationPlugin"
        }
        create("jvm-ecosystem") {
            id = "org.gradle.experimental.jvm-ecosystem"
            implementationClass = "org.gradle.api.experimental.jvm.JvmEcosystemPlugin"
        }
    }
}