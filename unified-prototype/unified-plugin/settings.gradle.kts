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
include("plugin-cpp")
include("plugin-common")
include("internal-testing-utils")
include("build-update-utils")

rootProject.name = "unified-plugin"
