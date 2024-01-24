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