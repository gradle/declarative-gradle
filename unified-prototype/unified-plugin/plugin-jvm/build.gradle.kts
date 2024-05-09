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
            displayName = "JVM Library Experimental Declarative Plugin"
            description = "Experimental declarative plugin for JVM libraries"
            implementationClass = "org.gradle.api.experimental.jvm.StandaloneJvmLibraryPlugin"
            tags = setOf("declarative-gradle", "java", "jvm")
        }
        create("java-library") {
            id = "org.gradle.experimental.java-library"
            displayName = "Java Library Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Java libraries"
            implementationClass = "org.gradle.api.experimental.java.StandaloneJavaLibraryPlugin"
            tags = setOf("declarative-gradle", "java", "jvm")
        }
        create("java-application") {
            id = "org.gradle.experimental.java-application"
            displayName = "Java Application Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Java applications"
            implementationClass = "org.gradle.api.experimental.java.StandaloneJavaApplicationPlugin"
            tags = setOf("declarative-gradle", "java", "jvm")
        }
        create("jvm-ecosystem") {
            id = "org.gradle.experimental.jvm-ecosystem"
            displayName = "JVM Ecosystem Experimental Declarative Plugin"
            description = "Experimental declarative plugin for the JVM ecosystem"
            implementationClass = "org.gradle.api.experimental.jvm.JvmEcosystemPlugin"
            tags = setOf("declarative-gradle", "java", "jvm")
        }
    }
}
