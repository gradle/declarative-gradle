package org.gradle.api.experimental.android

import org.gradle.test.fixtures.AbstractSpecification

class AndroidApplicationSpec extends AbstractSpecification {
    def 'can create an android application using viewBinding that generates an activity binding class'() {
        given:
        buildFile << """
            androidApplication {
                jdkVersion = 17
                compileSdk = 34
                
                namespace = "org.example.android.application"

                viewBinding {
                    enabled = true
                }
                
                dependencies {
                    implementation("androidx.appcompat:appcompat:1.6.1")
                    implementation("com.google.android.material:material:1.10.0")
                }
            }
        """

        file("src/main/res/layout/activity_main.xml").text = """<?xml version="1.0" encoding="utf-8"?>
            <androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:app="http://schemas.android.com/apk/res-auto"
                xmlns:tools="http://schemas.android.com/tools"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:fitsSystemWindows="true"
                tools:context=".MainActivity">
            </androidx.coordinatorlayout.widget.CoordinatorLayout>
        """

        file("src/main/java/org/example/android/application/MainActivity.java").text = """
            package org.example.android.application;
            
            import androidx.appcompat.app.AppCompatActivity;
            import org.example.android.application.databinding.ActivityMainBinding;
            
            class MainActivity extends AppCompatActivity {
                private ActivityMainBinding binding;
            }
        """

        file("src/main/AndroidManifest.xml").text = """<?xml version="1.0" encoding="utf-8"?>
            <manifest/>
        """

        expect:
        succeeds(":build")
    }

    def 'can create an android application using dataBinding'() {
        given:
        buildFile << """
            androidApplication {
                jdkVersion = 17
                compileSdk = 34
                
                namespace = "org.example.android.application"

                dataBinding {
                    enabled = true
                }
            }
        """

        file("src/main/AndroidManifest.xml").text = """<?xml version="1.0" encoding="utf-8"?>
            <manifest/>
        """

        expect:
        succeeds(":build")
    }

    def setup() {
        settingsFile << """
            plugins {
                id("org.gradle.experimental.android-ecosystem")
            }
    
            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                }
            }
    
            rootProject.name = "example"
        """

        file("gradle.properties").text = """
            android.useAndroidX=true
        """
    }
}