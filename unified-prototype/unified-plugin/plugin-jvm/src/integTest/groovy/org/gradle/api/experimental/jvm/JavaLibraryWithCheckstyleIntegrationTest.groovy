package org.gradle.api.experimental.jvm

import org.gradle.test.fixtures.AbstractSpecification

import static org.gradle.testkit.runner.TaskOutcome.*

class JavaLibraryWithCheckstyleIntegrationTest extends AbstractSpecification {
    def setup() {
        settingsFile << """
            plugins {
                id("org.gradle.experimental.jvm-ecosystem")
            }

            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                }
            }

            rootProject.name = "example"
        """
        file("config/checkstyle.xml") << """
            <!DOCTYPE module PUBLIC
                    "-//Puppy Crawl//DTD Check Configuration 1.2//EN"
                    "http://www.puppycrawl.com/dtds/configuration_1_2.dtd">
            <module name="Checker">
                <module name="SuppressionFilter">
                    <property name="file" value="\${config_loc}/suppressions.xml"/>
                </module>
                <module name="TreeWalker">
                    <module name="TypeName"/>
                </module>
            </module>
        """
        file("config/suppressions.xml") << """
            <!DOCTYPE suppressions PUBLIC
                "-//Puppy Crawl//DTD Suppressions 1.1//EN"
                "http://www.puppycrawl.com/dtds/suppressions_1_1.dtd">
            
            <suppressions>
                <suppress checks="TypeName"
                      files="bad_name.java"/>
            </suppressions>
        """
    }

    def "can apply checkstyle feature to javaLibrary"() {
        given:
        buildFile << """
            javaLibrary {
                checkstyle { }
            }    
        """
        file('src/main/java/org/example/example.java') << """
            package org.example;

            // This should cause a checkstyle failure
            public class example { }
        """

        when:
        def result = fails(":checkstyleMain")

        then:
        result.output.contains("Execution failed for task ':checkstyleMain'.")
        result.output.contains("Checkstyle rule violations were found. See the report at:")
        result.output.contains("Name 'example' must match pattern")
    }

    def "can suppress checkstyle violations"() {
        given:
        buildFile << """
            javaLibrary {
                checkstyle { }
            }    
        """
        file('src/main/java/org/example/bad_name.java') << """
            package org.example;

            // This should be suppressed by the checkstyle configuration
            public class bad_name { }
        """

        when:
        def result = succeeds(":checkstyleMain")

        then:
        result.task(':checkstyleMain').outcome == SUCCESS
    }

    def "checkstyle is incorporated into check lifecycle task"() {
        given:
        buildFile << """
            javaLibrary {
                checkstyle { }
            }    
        """
        file('src/main/java/org/example/NoViolations.java') << """
            package org.example;

            public class NoViolations { }
        """

        when:
        def result = succeeds(":check")

        then:
        result.task(':checkstyleMain').outcome == SUCCESS
    }
}
