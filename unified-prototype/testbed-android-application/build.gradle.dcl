androidApplication {
    jdkVersion = 11
    compileSdk = 34

    versionCode = 8
    versionName = "0.1.2"
    applicationId = "org.gradle.experimental.android.application"
    namespace = "org.gradle.experimental.android.application"

    dependencies {
        implementation("com.google.guava:guava:32.1.3-jre")
        implementation(project(":android-util"))
    }

    buildTypes {
        release {
            dependencies {
                implementation("com.squareup.okhttp3:okhttp:4.2.2")
                implementation("androidx.tracing:tracing-ktx:1.3.0-alpha02") // TODO: Is this necessary for minification(?) If so, this should be included automatically
            }

            minify {
                enabled = true
            }
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }
}
