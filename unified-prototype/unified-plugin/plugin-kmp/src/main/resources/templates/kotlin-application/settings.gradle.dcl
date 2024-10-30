pluginManagement {
    repositories {
        google() // Needed for the Android plugin, applied by the unified plugin
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.experimental.kmp-ecosystem").version("0.1.21")
}

dependencyResolutionManagement {
    repositories {
        google() // Needed for the linter plugin, used by the unified plugin
    }
}

rootProject.name = "example-kotlin-jvm-app"

include("app")
include("list")
include("utilities")

defaults {
    kotlinJvmApplication {
        javaVersion = 21

        dependencies {
            implementation("org.apache.commons:commons-text:1.11.0")
        }

        testing {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
            }
        }
    }

    kotlinJvmLibrary {
        javaVersion = 21

        dependencies {
            implementation("org.apache.commons:commons-text:1.11.0")
        }
        testing {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
            }
        }
    }
}
