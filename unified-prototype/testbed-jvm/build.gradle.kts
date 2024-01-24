plugins {
    id("org.gradle.jvm-prototype")
}

jvmLibrary {
    dependencies {
        api("org:foo:1.0")
    }
}