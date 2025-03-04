@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
    groovy // For spock testing
}

description = "Implements the declarative KMP DSL prototype"

dependencies {
    api(project(":plugin-common"))

    implementation(project(":plugin-jvm"))
    implementation(libs.kotlin.multiplatform)
    implementation(libs.kotlin.jvm)
    implementation(libs.apache.commons.text)
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
        create("kmp-library") {
            id = "org.gradle.experimental.kmp-library"
            displayName = "KMP Library Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Kotlin Multiplatform libraries"
            implementationClass = "org.gradle.api.experimental.kmp.StandaloneKmpLibraryPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
        create("kotlin-library") {
            id = "org.gradle.experimental.kotlin-jvm-library"
            displayName = "Kotlin JVM Library Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Kotlin JVM libraries"
            implementationClass = "org.gradle.api.experimental.kotlin.StandaloneKotlinJvmLibraryPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
        create("kotlin-application") {
            id = "org.gradle.experimental.kotlin-jvm-application"
            displayName = "Kotlin JVM Application Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Kotlin JVM applications"
            implementationClass = "org.gradle.api.experimental.kotlin.StandaloneKotlinJvmApplicationPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
        create("kmp-ecosystem") {
            id = "org.gradle.experimental.kmp-ecosystem"
            displayName = "KMP Ecosystem Experimental Declarative Plugin"
            description = "Experimental declarative plugin for the Kotlin Multiplatform ecosystem"
            implementationClass = "org.gradle.api.experimental.kmp.KmpEcosystemPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform")
        }
        create("kmp-ecosystem-init") {
            id = "org.gradle.experimental.kmp-ecosystem-init"
            displayName = "KMP Experimental Init Plugin"
            description = "Experimental init plugin for the Kotlin Multiplatform ecosystem"
            implementationClass = "org.gradle.api.experimental.kmp.KmpEcosystemInitPlugin"
            tags = setOf("declarative-gradle", "kotlin-multiplatform", "init")
        }
    }
}
