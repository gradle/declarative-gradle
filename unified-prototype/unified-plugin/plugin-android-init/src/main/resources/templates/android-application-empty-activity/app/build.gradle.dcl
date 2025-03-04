androidApplication {
    namespace = "org.example.app"

    compose {
        enabled = true
    }

    dependencies {
        implementation("androidx.core:core-ktx:1.10.1")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

        // Versions supplied by implicitly present Compose BOM applied by Compose feature
        implementation("androidx.activity:activity-compose:1.8.0")
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.ui:ui-graphics")
        implementation("androidx.compose.ui:ui-tooling-preview")
        implementation("androidx.compose.material3:material3")
    }

    buildTypes {
        release {
            minify {
                enabled = false
            }

            defaultProguardFiles = listOf(proguardFile("proguard-android-optimize.txt"))
            proguardFiles = listOf(proguardFile("proguard-rules.pro"))
        }

        debug {
            dependencies {
                implementation("androidx.compose.ui:ui-tooling")
                implementation("androidx.compose.ui:ui-test-manifest")
            }
        }
    }
}
