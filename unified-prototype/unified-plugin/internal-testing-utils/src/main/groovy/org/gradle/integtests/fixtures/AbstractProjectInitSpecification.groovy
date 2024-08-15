package org.gradle.integtests.fixtures

import org.gradle.internal.nativeintegration.console.TestOverrideConsoleDetector
import org.gradle.plugin.management.internal.autoapply.AutoAppliedPluginHandler
import org.gradle.test.fixtures.AbstractSpecification
import org.gradle.testkit.runner.GradleRunner

/**
 * Base class for tests that generate, build, and validate (via run, or something else) included project init specifications.
 */
abstract class AbstractProjectInitSpecification extends AbstractSpecification {
    private static final String DECLARATIVE_PROTOTYPE_VERSION = "0.1.11"

    protected File projectDir = file("new-project").tap { mkdirs() }

    abstract String getPluginId()

    // TODO: add project type method here, specify type of project to be generated (need to update Gradle wrapper to new nightly containing getType() first)

    def "can generate project from init project spec"() {
        when:
        runInitWithPluginAsInitProjectSpecSupplier()

        then:
        canBuildGeneratedProject()

        and:
        validateGeneratedProjectRuns()
    }

    protected void runInitWithPluginAsInitProjectSpecSupplier() {
        def args = ["init",
                    "-D${AutoAppliedPluginHandler.INIT_PROJECT_SPEC_SUPPLIERS_PROP}=$pluginId:$DECLARATIVE_PROTOTYPE_VERSION",
                    "-D${TestOverrideConsoleDetector.INTERACTIVE_TOGGLE}=true"] as String[]

        result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(args)
                .withPluginClasspath()
                .withDebug(true)
                .forwardOutput()
                .build()
    }

    protected void canBuildGeneratedProject() {
        result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments("build")
                .withDebug(true)
                .forwardOutput()
                .build()
    }

    protected void validateGeneratedProjectRuns() {
        result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(":app:run")
                .forwardOutput()
                .build()

        assert result.output.contains("Hello World!")
    }
}
