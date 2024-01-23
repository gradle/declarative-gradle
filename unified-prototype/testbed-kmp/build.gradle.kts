import org.gradle.api.experimental.kmp.kmpLibrary

plugins {
    id("org.gradle.unified-prototype")
}

kmpLibrary {
    dependencies {
        api("org:foo:1.0")
    }
}