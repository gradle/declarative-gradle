androidLibrary {
    jdkVersion = 11
    compileSdk = 34
    namespace = "org.gradle.experimental.android.library"

    dependencies {
        api("com.google.guava:guava:32.1.3-jre")
        implementation(project(":android-util"))
    }

    buildTypes {
        release {
            dependencies {
                implementation("com.squareup.okhttp3:okhttp:4.2.2")
            }

            minifyEnabled = true
        }
    }
}
