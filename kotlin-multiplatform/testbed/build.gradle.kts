plugins {
    id("org.gradle.playground-kmp")
}

repositories {
    mavenCentral()
    google()
}

defaultTasks("build")

kmpApplication {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines)
            }
        }
    }
}
