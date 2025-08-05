@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
    groovy // For spock testing
}

description = "Implements the declarative JVM DSL prototype"

dependencies {
    implementation(project(":plugin-common"))
    implementation("org.gradle.toolchains:foojay-resolver:1.0.0")
}

testing {
    suites {
        @Suppress("UnstableApiUsage")
        val integTest by registering(JvmTestSuite::class) {
            useSpock("2.2-groovy-3.0")

            dependencies {
                implementation(project(":internal-testing-utils"))
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(tasks.named("test"))
                        inputs.files(layout.settingsDirectory.file("version.txt"))
                    }
                }
            }
        }

        tasks.named("check") {
            dependsOn(integTest)
        }
    }
}

gradlePlugin {
    testSourceSets(project.sourceSets.getByName("integTest"))

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
        create("jvm-ecosystem-init") {
            id = "org.gradle.experimental.jvm-ecosystem-init"
            displayName = "JVM Experimental Init Plugin"
            description = "Experimental init plugin for the JVM ecosystem"
            implementationClass = "org.gradle.api.experimental.jvm.JvmEcosystemInitPlugin"
            tags = setOf("declarative-gradle", "java", "jvm", "init")
        }
    }
}
