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

includeBuild("build-logic")

include("plugin-android")
include("plugin-jvm")
include("plugin-kmp")
include("plugin-android-init")
include("plugin-swift")
include("plugin-cpp")
include("plugin-common")
include("internal-testing-utils")
include("build-update-utils")

rootProject.name = "unified-plugin"
