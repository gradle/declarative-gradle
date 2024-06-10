package org.gradle.api.experimental.android

import org.gradle.test.fixtures.AbstractSpecification

class AndroidLibrarySpec extends AbstractSpecification {
    def 'can create a basic android library with minimum required settings'() {
        given:
        buildFile << """
            androidLibrary {
                jdkVersion = 17
                compileSdk = 34
                
                namespace = "org.example.android.library"
            }
        """

        expect:
        succeeds(":build")
        file("build/outputs/aar/example-debug.aar").exists()
        file("build/outputs/aar/example-release.aar").exists()
    }

    def 'given conventions for requirements, can create a basic android library with empty block'() {
        given:
        buildFile << """
            androidLibrary {}
        """

        settingsFile << """
            conventions {
                androidLibrary {
                    jdkVersion = 17
                    compileSdk = 34
                    
                    namespace = "org.example.android.library"
                }
            }
        """

        expect:
        succeeds(":build")
        file("build/outputs/aar/example-debug.aar").exists()
        file("build/outputs/aar/example-release.aar").exists()
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
    }
}