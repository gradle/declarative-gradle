@file:Suppress("UnstableApiUsage")

plugins {
    groovy
    `java-test-fixtures`
    `java-library`
}

description = "Adds support for writing integration tests in unified-plugin projects using familiar patterns from gradle/gradle."

dependencies {
    api("junit:junit:4.13.2")
    api("commons-io:commons-io:2.8.0")
    api("commons-lang:commons-lang:2.6")
    api("com.google.guava:guava:30.1.1-jre")
    api("org.apache.ant:ant:1.10.13")
    api("org.jetbrains:annotations:24.0.1")
    api("org.spockframework:spock-core:2.2-groovy-4.0")
    api(gradleTestKit())
}

// Compile against Java 17 since Android requires Java 17 at minimum
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
