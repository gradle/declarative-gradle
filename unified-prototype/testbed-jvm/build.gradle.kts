plugins {
    id("org.gradle.experimental.jvm-library")
}

jvmLibrary {
    dependencies {
        api("org:foo:1.0")
    }
}