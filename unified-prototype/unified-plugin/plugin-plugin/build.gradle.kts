@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
    groovy // For spock testing
}

description = "Implements the declarative Plugin building Plugin"

dependencies {
    implementation(project(":plugin-common"))
    implementation("org.gradle.toolchains:foojay-resolver:0.8.0")
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
        create("gradle-plugin") {
            id = "org.gradle.experimental.java-gradle-plugin"
            displayName = "Gradle Java Plugin Experimental Declarative Plugin"
            description = "Experimental declarative plugin for building Gradle plugins in Java"
            implementationClass = "org.gradle.api.experimental.plugin.JavaGradlePluginPlugin"
            tags = setOf("declarative-gradle", "java", "plugin")
        }
        create("plugin-ecosystem") {
            id = "org.gradle.experimental.plugin-ecosystem"
            displayName = "Gradle Plugin Experimental Declarative Plugin"
            description = "Experimental declarative plugin for the building Gradle plugins"
            implementationClass = "org.gradle.api.experimental.plugin.GradlePluginEcosystemPlugin"
            tags = setOf("declarative-gradle", "plugin")
        }
    }
}
