pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("unified-plugin")
}

rootProject.name = "unified-prototype"

include("testbed-android")
include("testbed-android-groovy")
include("testbed-kmp")
include("testbed-kmp-groovy")
include("testbed-jvm")
include("testbed-jvm-groovy")
