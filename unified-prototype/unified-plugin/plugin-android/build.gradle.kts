@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    id("build-logic.publishing")
    groovy // For spock testing
}

description = "Implements the declarative Android DSL prototype"

dependencies {
    api(project(":plugin-common"))
    api(libs.android.agp.application)
    api(libs.android.kotlin.android)

    implementation(project(":plugin-jvm"))
    implementation(libs.baseline.profile.plugin)
    implementation(libs.dependency.guard.plugin)
    implementation(libs.ksp.plugin)
    implementation(libs.hilt.android.plugin)
    implementation(libs.kotlin.serialization.plugin)
    implementation(libs.room.plugin)
    implementation(libs.protobuf.plugin)
    implementation(libs.roborazzi.plugin)
    implementation(libs.google.services.plugin)
    implementation(libs.firebase.perf.plugin)
    implementation(libs.firebase.crashlytics.plugin)
    implementation(libs.oss.licenses.plugin)
    implementation(libs.compose.compiler.plugin)
    implementation(libs.secrets.plugin)

    implementation(libs.apache.commons.lang)
    implementation(libs.android.tools.common)
    implementation(libs.truth)
}

testing {
    suites {
        @Suppress("UnstableApiUsage")
        val integTest by registering(JvmTestSuite::class) {
            useSpock("2.2-groovy-4.0")

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
        create("android-library") {
            id = "org.gradle.experimental.android-library"
            displayName = "Android Library Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Android libraries"
            implementationClass = "org.gradle.api.experimental.android.library.StandaloneAndroidLibraryPlugin"
            tags = setOf("declarative-gradle", "android")
        }
        create("android-application") {
            id = "org.gradle.experimental.android-application"
            displayName = "Android Application Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Android applications"
            implementationClass = "org.gradle.api.experimental.android.application.StandaloneAndroidApplicationPlugin"
            tags = setOf("declarative-gradle", "android")
        }
        create("android-test") {
            id = "org.gradle.experimental.android-test"
            displayName = "Android Test Experimental Declarative Plugin"
            description = "Experimental declarative plugin for Android test projects"
            implementationClass = "org.gradle.api.experimental.android.test.StandaloneAndroidTestPlugin"
            tags = setOf("declarative-gradle", "android")
        }
        create("android-ecosystem") {
            id = "org.gradle.experimental.android-ecosystem"
            displayName = "Android Ecosystem Experimental Declarative Plugin"
            description = "Experimental declarative plugin for the Android ecosystem"
            implementationClass = "org.gradle.api.experimental.android.AndroidEcosystemPlugin"
            tags = setOf("declarative-gradle", "android")
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
