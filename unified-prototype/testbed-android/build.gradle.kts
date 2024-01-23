import org.gradle.api.experimental.android.androidLibrary

plugins {
    id("org.gradle.unified-prototype")
}

androidLibrary {
    dependencies {
        api("org:foo:1.0")
    }
}