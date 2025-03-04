androidApplication {
    namespace = "org.example.app"

    viewBinding {
        enabled = true
    }

    dependencies {
        implementation("androidx.core:core-ktx:1.10.1")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.10.0")

        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
        implementation("androidx.navigation:navigation-ui-ktx:2.6.0")
    }

    buildTypes {
        release {
            minify {
                enabled = false
            }

            defaultProguardFiles = listOf(proguardFile("proguard-android-optimize.txt"))
            proguardFiles = listOf(proguardFile("proguard-rules.pro"))
        }
    }
}
