import org.gradle.api.experimental.jvm.jvmLibrary

plugins {
    id("org.gradle.unified-prototype")
}

jvmLibrary {
    dependencies {
        api("org:foo:1.0")
    }
}