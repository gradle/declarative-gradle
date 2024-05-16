androidLibrary {
    jdkVersion = 11
    compileSdk = 34
    namespace = "org.gradle.experimental.android.library"

    dependencies {
        api("com.google.guava:guava:32.1.3-jre")
        implementation(project(":android-util"))
    }

    kotlinSerialization {
        version = "1.6.3"
        json()
    }

    buildTypes {
        release {
            dependencies {
                implementation("com.squareup.okhttp3:okhttp:4.2.2")
            }

            minify {
                enabled = true
            }
        }
    }

    testing {
        dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
        }
    }
}
