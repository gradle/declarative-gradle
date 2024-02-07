plugins {
    id("org.gradle.experimental.jvm-library")
}

jvmLibrary {
    dependencies {
        implementation("org.apache.commons:commons-lang3:3.14.0")
    }
    targets {
        java(11) {
            dependencies {
                // Requires java 11
                implementation("org.hibernate.orm:hibernate-core:6.4.2.Final")
            }
        }
        java(17) {
            dependencies {
                // Requires java 17
                implementation("org.springframework.boot:spring-boot:3.2.2")
            }
        }
    }
}

// These tasks are to showcase that we can actually execute
// the above libraries. If we had applications or implemented testing,
// we would not need these tasks.

tasks.register<JavaExec>("executeJava11") {
    mainClass = "com.example.Entrypoint"
    classpath = (configurations["java11RuntimeClasspath"] + sourceSets["java11"].output)
    javaLauncher = javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.register<JavaExec>("executeJava17") {
    mainClass = "com.example.Entrypoint"
    classpath = (configurations["java17RuntimeClasspath"] + sourceSets["java17"].output)
    javaLauncher = javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}