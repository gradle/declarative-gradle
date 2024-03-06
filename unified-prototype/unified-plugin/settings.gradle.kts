dependencyResolutionManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

include("plugin-android")
include("plugin-jvm")
include("plugin-kmp")
include("plugin-common")

include(":plugin-hilt")
project(":plugin-hilt").projectDir = File("now-in-android-plugins", "plugin-hilt")

rootProject.name = "unified-plugin"