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
            defaults {
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

    def 'given conventions with an optional extension block that configures but does not enable that extension, can create a basic android library that enables that extension'() {
        given:
        file("gradle.properties") << """
            android.useAndroidX=true
        """

        buildFile << """
            androidLibrary {
                hilt {
                    enabled = true
                }
            }
        """

        settingsFile << """
            defaults {
                androidLibrary {
                    jdkVersion = 17
                    compileSdk = 34
                    
                    namespace = "org.example.android.library"
                    
                    hilt {
                        enabled = false
                    }
                }
            }
        """

        file("src/main/kotlin/org/example/TestHiltSupport.kt") << """
            package org.example
            
            import dagger.Module // Should be able to import this class
            
            class TestSupport {}
        """

        expect:
        succeeds(":build")
        file("build/outputs/aar/example-debug.aar").exists()
        file("build/outputs/aar/example-release.aar").exists()
    }

    def 'given conventions with an optional extension block that configures but does not enable that extension, can not create a basic android library that uses that extension without explicitly enabling it'() {
        given:
        file("gradle.properties") << """
            android.useAndroidX=true
        """

        buildFile << """
            androidLibrary {
                hilt {}
            }
        """

        settingsFile << """
            defaults {
                androidLibrary {
                    jdkVersion = 17
                    compileSdk = 34
                    
                    namespace = "org.example.android.library"
                    
                    hilt {
                        enabled = false
                    }
                }
            }
        """

        file("src/main/kotlin/org/example/TestHiltSupport.kt") << """
            package org.example
            
            import dagger.Module // Should be able to import this class
            
            class TestSupport {}
        """

        expect:
        fails(":compileReleaseKotlin")
    }

    def 'can create a library with compose enabled'() {
        given:
        buildFile << """
            androidLibrary {
                jdkVersion = 17
                compileSdk = 34
                
                namespace = "org.example.android.library"
                
                compose {
                    enabled = true
                }
                
                testing {
                    failOnNoDiscoveredTests = false
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

        file("gradle.properties").text = """
            android.useAndroidX=true
        """
    }
}