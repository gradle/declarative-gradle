@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
    groovy // For spock testing
    `java-test-fixtures`
}

description = "Common APIs and implementation classes shared by the ecosystem specific declarative prototypes"

dependencies {
    implementation("commons-io:commons-io:2.15.1")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useSpock("2.2-groovy-4.0")

            dependencies {
                implementation("commons-io:commons-io:2.15.1")
                implementation(testFixtures(project()))
            }
        }

        val integTest by registering(JvmTestSuite::class) {
            useSpock("2.2-groovy-4.0")

            dependencies {
                implementation("commons-io:commons-io:2.15.1")
                implementation(project(":plugin-jvm"))
                implementation(testFixtures(project()))
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
    plugins {
        create("common") {
            id = "org.gradle.experimental.declarative-common"
            displayName = "Common Experimental Declarative Plugin"
            description = "Experimental declarative plugin containing common code shared by the Android, JVM, and KMP prototype plugins"
            implementationClass = "org.gradle.api.experimental.common.CommonPlugin"
            tags = setOf("declarative-gradle")
        }
    }
}
