pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("build-logic")
}

rootProject.name = "android-prototype"

include("testbed")