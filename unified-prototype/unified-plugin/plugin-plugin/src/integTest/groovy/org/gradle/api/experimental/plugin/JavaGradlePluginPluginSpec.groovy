package org.gradle.api.experimental.plugin

import org.gradle.test.fixtures.AbstractSpecification

/**
 * Integration tests for the Gradle plugin ecosystem's {@code javaGradlePlugin} plugin project type.
 * plugin project type.
 */
class JavaGradlePluginPluginSpec extends AbstractSpecification {
    def "gradle java plugin project builds"() {
        given:
        settingsFile << """
            plugins {
                id("org.gradle.experimental.plugin-ecosystem")
            }
            
            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                }
            }
    
            rootProject.name = "example-plugin"
        """

        buildFile << """
            javaGradlePlugin {
                description = "An example project defining a Gradle plugin writen in Java"
                
                dependencies {
                    implementation("com.google.guava:guava:33.4.0-jre")
                }
                
                registers {
                    id("org.gradle.example") {
                        description = "An example plugin"
                        implementationClass = "org.gradle.example.ExamplePlugin"
                    }
                }
            }
        """

        file("src/main/java/org/gradle/example/ExamplePlugin.java") << defineExamplePlugin()

        expect:
        succeeds(":build")
    }

    def "gradle java plugin project can be included and applied"() {
        given:
        settingsFile << """
            pluginManagement {
                includeBuild("plugin")
            }
            
            include("example-project")
            
            rootProject.name = "example-plugin-use"
        """

        and:
        file("example-project/build.gradle") << """
            plugins {
                id("org.gradle.example")
            }
        """

        and:
        file("plugin/build.gradle.dcl") << """
            javaGradlePlugin {
                description = "An example project defining a Gradle plugin writen in Java"
                
                dependencies {
                    implementation("com.google.guava:guava:33.4.0-jre")
                }
                
                registers {
                    id("org.gradle.example") {
                        description = "An example plugin"
                        implementationClass = "org.gradle.example.ExamplePlugin"
                    }
                }
            }
        """

        file("plugin/src/main/java/org/gradle/example/ExamplePlugin.java") << defineExamplePlugin()
        file("plugin/settings.gradle.dcl") << defineExamplePluginSettings()

        expect:
        succeeds(":example-project:help")
        result.getOutput().contains("Hello from ExamplePlugin")
    }

    private String defineExamplePlugin() {
        return """
            package org.gradle.example;
            
            import org.gradle.api.Plugin;
            import org.gradle.api.Project;
            
            public class ExamplePlugin implements Plugin<Project> {
                @Override
                public void apply(Project project) {
                    project.getLogger().lifecycle("Hello from ExamplePlugin");
                }
            }
        """
    }

    private String defineExamplePluginSettings() {
        return """
            plugins {
                id("org.gradle.experimental.plugin-ecosystem")
            }
            
            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                }
            }
            
            rootProject.name = "example-plugin"
        """
    }
}
