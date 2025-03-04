pluginManagement {
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google() {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

include("plugin-android")
include("plugin-jvm")
include("plugin-kmp")
include("plugin-android-init")
include("plugin-swift")
include("plugin-cpp")
include("plugin-common")
include("plugin-plugin")
include("internal-testing-utils")

rootProject.name = "unified-plugin"
