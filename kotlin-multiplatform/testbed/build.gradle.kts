plugins {
    id("org.gradle.playground-kmp")
}

repositories {
    mavenCentral()
    google()
}

kmpApplication {
    sourceSets {
        commonMain {
            dependencies {

            }
        }
    }
}
