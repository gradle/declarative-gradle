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

    abstract String getProjectSpecType()
    abstract String getEcosystemPluginId()

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
        def initInvocation = ["-D${AutoAppliedPluginHandler.INIT_PROJECT_SPEC_SUPPLIERS_PROP}=$ecosystemPluginId:$DECLARATIVE_PROTOTYPE_VERSION",
                    "init",
                    "--type", "$projectSpecType"] as String[]

        result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(initInvocation)
                .forwardOutput()
                .build()
    }

    protected void canBuildGeneratedProject() {
        result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments("build")
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
