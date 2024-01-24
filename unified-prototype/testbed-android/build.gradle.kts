plugins {
    id("org.gradle.android-prototype")
}

androidLibrary {
    dependencies {
        api("org:foo:1.0")
    }
}