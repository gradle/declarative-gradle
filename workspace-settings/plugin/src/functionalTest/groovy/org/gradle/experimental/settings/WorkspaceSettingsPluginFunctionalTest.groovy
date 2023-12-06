package org.gradle.experimental.settings

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

class WorkspaceSettingsPluginFunctionalTest extends Specification {
    @TempDir
    File projectDir

    private File getBuildFile() {
        return new File(projectDir, "build.gradle.kts")
    }

    private File getSettingsFile() {
        return new File(projectDir, "settings.gradle.kts")
    }

    def "can configure a single project workspace via extension"() {
        given:
        settingsFile << """
            plugins {
                id("org.gradle.experimental.settings.workspace")
            }
            
            configure<org.gradle.experimental.settings.WorkspaceSettings> {
                build("foo") {
                }
                projects {
                    
                }
            }
            
            require(rootProject.name == "foo") { "Expected root project to be named 'foo', but was named \${rootProject.name}" }
        """
        buildFile << """
            require(allprojects.size == 1) { "Expected 1 project, but found \${allprojects.size}" }
        """

        expect:
        BuildResult result = createRunner()
                .withArguments("--stacktrace", "projects")
                .build()
    }

    def "can configure a multi-project workspace via extension"() {
        given:
        settingsFile << """
            plugins {
                id("org.gradle.experimental.settings.workspace")
            }
            
            configure<org.gradle.experimental.settings.WorkspaceSettings> {
                build("foo") {
                }
                projects {
                    subproject("bar")
                    subproject("baz") {
                        // should path be relative to root or parent project?
                        subproject("qux")
                        // should logical be :baz:qax?
                        subproject("../qax")
                    }
                    // should logical be :fuzz or :buzz:fuzz?
                    directory("buzz") {
                        subproject("fuzz")
                    }
                }
            }
            
            require(rootProject.name == "foo") { "Expected root project to be named 'foo', but was named \${rootProject.name}" }
            require(project(":bar").projectDir == rootProject.projectDir.resolve("bar")) { "Expected project ':bar' to be located at 'bar', but was located at \${project(":bar").projectDir}" }
            require(project(":baz").projectDir == rootProject.projectDir.resolve("baz")) { "Expected project ':baz' to be located at 'baz', but was located at \${project(":baz").projectDir}" }
            require(project(":baz:qux").projectDir == rootProject.projectDir.resolve("baz/qux")) { "Expected project ':baz:qux' to be located at 'baz/qux', but was located at \${project(":baz:qux").projectDir}" }
            require(project(":baz:qax").projectDir == rootProject.projectDir.resolve("qax")) { "Expected project ':baz:qax' to be located at 'qax', but was located at \${project(":baz:qax").projectDir}" }
            require(project(":fuzz").projectDir == rootProject.projectDir.resolve("buzz/fuzz")) { "Expected project ':fuzz' to be located at 'buzz/fuzz', but was located at \${project(":fuzz").projectDir}" }
        """
        buildFile << """
            require(allprojects.size == 6) { "Expected 5 projects, but found \${allprojects.size}" }
        """

        expect:
        BuildResult result = createRunner()
                .withArguments("--stacktrace", "projects")
                .build()
    }

    def "handles the same project configured twice"() {
        given:
        settingsFile << """
            plugins {
                id("org.gradle.experimental.settings.workspace")
            }
            
            configure<org.gradle.experimental.settings.WorkspaceSettings> {
                build("foo") {
                }
                projects {
                    subproject("bar")
                    subproject("bar")
                }
            }
            
            require(findProject(":bar") != null) { "Expected project ':bar' to be configured, but was not" }
        """
        buildFile << """
            require(allprojects.size == 2) { "Expected 2 projects, but found \${allprojects.size}" }
        """

        expect:
        BuildResult result = createRunner()
                .withArguments("--stacktrace", "projects")
                .build()
    }

    def "autodetects a multi-project workspace"() {
        given:
        // directories that should be detected
        createBuildFileIn("foo")
        createBuildFileIn("foo/bar")
        createBuildFileIn("baz/qux")

        settingsFile << """
            plugins {
                id("org.gradle.experimental.settings.workspace")
            }

            configure<org.gradle.experimental.settings.WorkspaceSettings>() {
                projects {
                    subproject("foo") {
                        autodetect = true
                    }
                    directory("baz") {
                        autodetect = true
                    }
                }
            }

            require(project(":foo").projectDir == rootProject.projectDir.resolve("foo")) { "Expected project ':foo' to be located at 'foo', but was located at \${project(":foo").projectDir}" }
            require(project(":foo:bar").projectDir == rootProject.projectDir.resolve("foo/bar")) { "Expected project ':foo:bar' to be located at 'foo/bar', but was located at \${project(":foo:bar").projectDir}" }
            require(project(":qux").projectDir == rootProject.projectDir.resolve("baz/qux")) { "Expected project ':qux' to be located at 'baz/qux', but was located at \${project(":baz:qux").projectDir}" }
        """
        buildFile << """
            require(allprojects.size == 4) { "Expected 4 projects, but found \${allprojects.size}" }
        """

        expect:
        BuildResult result = createRunner()
                .withArguments("--stacktrace", "projects")
                .build()
    }

    def "does not autodetect when not configured"() {
        given:
        // directories that should be detected
        createBuildFileIn("foo")
        createBuildFileIn("foo/bar")
        createBuildFileIn("baz/qux")

        settingsFile << """
            plugins {
                id("org.gradle.experimental.settings.workspace")
            }

            configure<org.gradle.experimental.settings.WorkspaceSettings>() {
                projects {
                    subproject("foo") 
                    subproject("baz")
                }
            }

            require(project(":foo").projectDir == rootProject.projectDir.resolve("foo")) { "Expected project ':foo' to be located at 'foo', but was located at \${project(":foo").projectDir}" }
            require(findProject(":foo:bar") == null) { "Expected project ':foo:bar' to not be configured, but was" }
            require(project(":baz").projectDir == rootProject.projectDir.resolve("baz")) { "Expected project ':qux' to be located at 'baz', but was located at \${project(":baz").projectDir}" }
            require(findProject(":baz:qux") == null) { "Expected project ':baz:qux' to not be configured, but was" }
        """
        buildFile << """
            require(allprojects.size == 3) { "Expected 3 projects, but found \${allprojects.size}" }
        """

        expect:
        BuildResult result = createRunner()
                .withArguments("--stacktrace", "projects")
                .build()
    }

    private void createBuildFileIn(String path) {
        Path subdir = projectDir.toPath().resolve(path)
        Files.createDirectories(subdir)
        Path buildFile = subdir.resolve("build.gradle.kts")
        Files.createFile(buildFile)
    }

    private GradleRunner createRunner() {
        return GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(projectDir)
    }
}
