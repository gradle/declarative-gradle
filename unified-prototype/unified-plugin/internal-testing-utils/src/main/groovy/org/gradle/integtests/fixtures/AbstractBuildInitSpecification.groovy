package org.gradle.integtests.fixtures

import org.gradle.buildinit.specs.internal.BuildInitSpecRegistry
import org.gradle.test.fixtures.AbstractSpecification
import org.gradle.testkit.runner.GradleRunner

/**
 * Base class for tests that generate, build, and validate (via run, or something else) included project init specifications.
 */
abstract class AbstractBuildInitSpecification extends AbstractSpecification {
    private static final String DECLARATIVE_PROTOTYPE_VERSION = "use.local.version"

    protected File projectDir = file("new-project").tap { mkdirs() }

    protected abstract String getProjectSpecType()
    protected abstract String getEcosystemPluginId()

    /**
     * Perform additional validation on the built project, perhaps by running it and verifying the output.
     * <p>
     * Defaults to No-Op.
     */
    protected void validateBuild() {}

    def "can generate project from init project spec"() {
        when:
        runInitWithPluginAsBuildInitSpecSupplier()

        then:
        canBuildGeneratedProject()

        and:
        true
        validateBuild()
    }

    protected void runInitWithPluginAsBuildInitSpecSupplier() {
        def initInvocation = ["-D${BuildInitSpecRegistry.BUILD_INIT_SPECS_PLUGIN_SUPPLIER}=$ecosystemPluginId:$DECLARATIVE_PROTOTYPE_VERSION",
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
                .withPluginClasspath()
                .withArguments("build")
                .forwardOutput()
                .build()
    }
}
