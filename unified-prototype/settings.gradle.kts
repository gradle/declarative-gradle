pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("unified-plugin")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "unified-prototype"

include("testbed-android-library")
include("testbed-android-application")
include("testbed-kmp")
include("testbed-jvm")
include("testbed-jvm-groovy")
include("testbed-java-application")
