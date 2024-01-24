plugins {
    id("org.gradle.experimental.android-library")
}

androidLibrary {
    dependencies {
        api("org:foo:1.0")
    }
}