import org.gradle.api.experimental.common.plusAssign

plugins {
    id("org.gradle.playground-android")
}

repositories {
    mavenCentral()
    google()
}

androidApplication {
    namespace = "com.example"
    compileSdk = 30

    // Replaces compileOptions & kotlinOptions
    jdkVersion = 17

    // defaultConfig
    minSdk = 25

    sources {
        kotlin += file("src/main3/kotlin")
    }

    dependencies {
        implementation("org.apache.commons:commons-lang3:3.9")
        compileOnly("com.google.guava:guava:28.0-jre")
        runtimeOnly("com.squareup.okhttp3:okhttp:4.2.2")
    }

    targets {
        demoRelease {
            minSdk = 25
            dependencies {
                implementation("com.squareup.retrofit2:retrofit:2.6.2")
            }
        }
        fullDebug {
            minSdk = 25
            dependencies {
                implementation("com.squareup.okio:okio:2.4.3")
            }
        }
    }

    // This doesn't do anything, but shows an example of a NDOC instead of hard-coded targets
    targets2 {
        named("demoRelease") {
            minSdk = 25
            dependencies {
                implementation("com.squareup.retrofit2:retrofit:2.6.2")
            }
        }
    }
}

