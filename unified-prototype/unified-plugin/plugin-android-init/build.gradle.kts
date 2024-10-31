@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
    groovy // For spock testing
}

description = "Provides Build Init Specs for Android projects"

dependencies {
    api(project(":plugin-common"))
}

testing {
    suites {
        @Suppress("UnstableApiUsage")
        val integTest by registering(JvmTestSuite::class) {
            useSpock("2.2-groovy-3.0")

            dependencies {
                implementation(project(":internal-testing-utils"))
            }
        }

        tasks.getByPath("check").dependsOn(integTest)
    }
}

gradlePlugin {
    testSourceSets(project.sourceSets.getByName("integTest"))

    plugins {
        create("android-init") {
            id = "org.gradle.experimental.android-ecosystem-init"
            displayName = "Android Experimental Build Init Plugin"
            description = "Experimental build init plugin contribuing build types for the Android ecosystem"
            implementationClass = "org.gradle.api.experimental.android.AndroidEcosystemInitPlugin"
            tags = setOf("declarative-gradle", "android", "init")
        }
    }
}

// Compile against Java 17 since Android requires Java 17 at minimum
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
