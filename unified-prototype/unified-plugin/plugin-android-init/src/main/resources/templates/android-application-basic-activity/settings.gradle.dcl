pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.experimental.android-ecosystem").version("0.1.45")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "Android Application with Basic Activity"

include(":app")

defaults {
    androidApplication {
        jdkVersion = 17
        compileSdk = 34
        minSdk = 31

        versionCode = 1
        versionName = "1.0"
        applicationId = "org.gradle.experimental.android.app"

        testing {
            dependencies {
                implementation("junit:junit:4.13.2")
                androidImplementation("androidx.test.ext:junit:1.1.5")
                androidImplementation("androidx.test.espresso:espresso-core:3.5.1")
            }

            testOptions {
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
        }
    }
}
