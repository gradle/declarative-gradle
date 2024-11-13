pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://androidx.dev/studio/builds/12648882/artifacts/artifacts/repository")
        }
    }
}

plugins {
    id("com.android.ecosystem").version("8.9.0-dev")
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://androidx.dev/studio/builds/12648882/artifacts/artifacts/repository")
        }
    }
}

rootProject.name = "example-android-app"

include("app")
include("list")
include("utilities")

defaults {
    androidApp {
        compileSdk = 34
        compileOptions {
            sourceCompatibility = VERSION_17
            targetCompatibility = VERSION_17
        }
        defaultConfig {
            minSdk = 30
            versionCode = 1
            versionName = "0.1"
            applicationId = "org.gradle.experimental.android.app"
        }
        dependenciesDcl {
            implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
            testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
            testImplementation("org.junit.platform:junit-platform-launcher")
            androidTestImplementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
        }
    }

    androidLibrary {
        compileSdk = 34
        compileOptions {
            sourceCompatibility = VERSION_17
            targetCompatibility = VERSION_17
        }
        defaultConfig {
            minSdk = 30
        }
        dependenciesDcl {
            implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
            testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
            testImplementation("org.junit.platform:junit-platform-launcher")
            androidTestImplementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
        }
    }
}
