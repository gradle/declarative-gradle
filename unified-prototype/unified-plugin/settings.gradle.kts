dependencyResolutionManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

includeBuild("build-logic")
include("plugin-android")
include("plugin-jvm")
include("plugin-kmp")
include("plugin-swift")
include("plugin-common")
include("internal-testing-utils")

rootProject.name = "unified-plugin"
