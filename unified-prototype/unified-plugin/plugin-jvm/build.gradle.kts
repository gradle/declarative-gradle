plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
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
            tags = setOf("declarative-gradle", "java", "jvm")
        }
        create("java-library") {
            id = "org.gradle.experimental.java-library"
            implementationClass = "org.gradle.api.experimental.java.StandaloneJavaLibraryPlugin"
            tags = setOf("declarative-gradle", "java", "jvm")
        }
        create("java-application") {
            id = "org.gradle.experimental.java-application"
            implementationClass = "org.gradle.api.experimental.java.StandaloneJavaApplicationPlugin"
            tags = setOf("declarative-gradle", "java", "jvm")
        }
        create("jvm-ecosystem") {
            id = "org.gradle.experimental.jvm-ecosystem"
            implementationClass = "org.gradle.api.experimental.jvm.JvmEcosystemPlugin"
            tags = setOf("declarative-gradle", "java", "jvm")
        }
    }
}
