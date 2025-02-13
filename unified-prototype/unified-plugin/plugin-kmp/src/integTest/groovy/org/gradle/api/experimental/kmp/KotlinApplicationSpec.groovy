package org.gradle.api.experimental.kmp

import org.gradle.test.fixtures.AbstractSpecification
import org.gradle.testkit.runner.TaskOutcome

/**
 * Integration tests for the KMP ecosystem plugin's {@code kotlinApplication} project type.
 */
class KotlinApplicationSpec extends AbstractSpecification {
    def 'kmp jvm application with a jvmTest missing a dep fails to compile'() {
        given:
        buildFile << """
            kotlinApplication {
                targets {
                    jvm {
                        jdkVersion = 17
                        
                        testing {
                            dependencies {
                                implementation("org.junit.jupiter:junit-jupiter:5.11.4")
                                runtimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
                            }
                        }
                    }
                }
            }
        """

        file("src/jvmTest/kotlin/org/example/Test.kt") << customGuavaTest(true)

        expect:
        fails("build")

        result.task(":compileTestKotlinJvm").outcome == TaskOutcome.FAILED
        !result.output.contains("This test should fail")
    }

    def 'kmp jvm application with a failing jvmTest fails at runtime'() {
        given:
        buildFile << """
            kotlinApplication {
                targets {
                    jvm {
                        jdkVersion = 17
                        
                        testing {
                            dependencies {
                                implementation("com.google.guava:guava:33.4.0-jre")
                                implementation("org.junit.jupiter:junit-jupiter:5.11.4")
                                runtimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
                            }
                        }
                    }
                }
            }
        """

        file("src/jvmTest/kotlin/org/example/Test.kt") << customGuavaTest(true)

        expect:
        fails("build")

        result.task(":allTests").outcome == TaskOutcome.FAILED
    }

    def 'kmp jvm application with a passing jvmTest passes'() {
        given:
        buildFile << """
            kotlinApplication {
                targets {
                    jvm {
                        jdkVersion = 17
                        
                        testing {
                            dependencies {
                                implementation("com.google.guava:guava:33.4.0-jre")
                                implementation("org.junit.jupiter:junit-jupiter:5.11.4")
                                runtimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
                            }
                        }
                    }
                }
            }
        """

        file("src/jvmTest/kotlin/org/example/Test.kt") << customGuavaTest(false)

        expect:
        succeeds(":allTests")
    }

    def 'kmp jvm application with a custom test JVM test suite missing a dep fails to compile'() {
        given:
        buildFile << """
            kotlinApplication {
                targets {
                    jvm {
                        jdkVersion = 17
                        
                        testing {
                            functionalTest {
                                dependencies {
                                    implementation("org.junit.jupiter:junit-jupiter:5.11.4")
                                    runtimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
                                }
                            }
                        }
                    }
                }
            }
        """

        file("src/jvmFunctionalTest/kotlin/org/example/Test.kt") << customGuavaTest()

        expect:
        fails(":compileSuiteFunctionalTestCompilationKotlinJvm")
    }

    def 'kmp jvm application with a failing custom test JVM test suite fails at runtime'() {
        given:
        buildFile << """
            kotlinApplication {
                targets {
                    jvm {
                        jdkVersion = 17
                        
                        testing {
                            functionalTest {
                                dependencies {
                                    implementation("com.google.guava:guava:33.4.0-jre")
                                    implementation("org.junit.jupiter:junit-jupiter:5.11.4")
                                    runtimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
                                }
                            }
                        }
                    }
                }
            }
        """

        file("src/jvmFunctionalTest/kotlin/org/example/Test.kt") << customGuavaTest(true)

        expect:
        fails(":suiteFunctionalTestTest")
    }

    def 'kmp jvm application with a passing custom test JVM test suite passes'() {
        given:
        buildFile << """
            kotlinApplication {
                targets {
                    jvm {
                        jdkVersion = 17
                        
                        testing {
                            functionalTest {
                                dependencies {
                                    implementation("com.google.guava:guava:33.4.0-jre")
                                    implementation("org.junit.jupiter:junit-jupiter:5.11.4")
                                    runtimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
                                }
                            }
                        }
                    }
                }
            }
        """

        file("src/jvmFunctionalTest/kotlin/org/example/Test.kt") << customGuavaTest(false)

        expect:
        succeeds(":suiteFunctionalTestTest")
    }

    def 'kmp jvm application with a passing custom test JVM test suite inherits dependencies from common deps'() {
        given:
        buildFile << """
            kotlinApplication {
                targets {
                    jvm {
                        jdkVersion = 17
                        
                        dependencies {
                            implementation("com.google.guava:guava:33.4.0-jre")
                        }
                        
                        testing {
                            functionalTest {
                                dependencies {
                                    implementation("org.junit.jupiter:junit-jupiter:5.11.4")
                                    runtimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
                                }
                            }
                        }
                    }
                }
            }
        """

        file("src/jvmFunctionalTest/kotlin/org/example/Test.kt") << customGuavaTest(false)

        expect:
        succeeds(":suiteFunctionalTestTest")
    }

    def 'kmp jvm application with a passing custom test JVM test suite inherits dependencies from common test deps'() {
        given:
        buildFile << """
            kotlinApplication {
                targets {
                    jvm {
                        jdkVersion = 17
                        
                        testing {
                            dependencies {
                                implementation("com.google.guava:guava:33.4.0-jre")
                            }
                        
                            functionalTest {
                                dependencies {
                                    implementation("org.junit.jupiter:junit-jupiter:5.11.4")
                                    runtimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
                                }
                            }
                        }
                    }
                }
            }
        """

        file("src/jvmFunctionalTest/kotlin/org/example/Test.kt") << customGuavaTest(false)

        expect:
        succeeds(":suiteFunctionalTestTest")
    }

    def setup() {
        settingsFile << """
            plugins {
                id("org.gradle.experimental.kmp-ecosystem")
            }
    
            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                }
            }
    
            rootProject.name = "example"
        """
    }

    private String customGuavaTest(boolean fails = false) {
        return """
            package org.example
            
            import com.google.common.base.Preconditions
            import org.junit.jupiter.api.Test
            
            class Test {
                @Test
                fun test() {
                    ${ fails ? 'Preconditions.checkState(false, "This test should fail")' : ''}
                }
            }
        """
    }
}
