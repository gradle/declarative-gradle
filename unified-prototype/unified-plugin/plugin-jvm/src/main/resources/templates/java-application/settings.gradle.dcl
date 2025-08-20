pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.experimental.jvm-ecosystem").version("0.1.45")
}

rootProject.name = "example-java-app"

include("app")
include("list")
include("utilities")

defaults {
    javaLibrary {
        javaVersion = 17

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

    javaApplication {
        javaVersion = 17

        dependencies {
            implementation("org.apache.commons:commons-text:1.11.0")
        }

        testing {
            testJavaVersion = 21
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
            }
        }
    }
}
