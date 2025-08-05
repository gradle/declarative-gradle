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
        if (shouldValidateLatestPublishedVersionUsedInSpec()) {
            validateLatestPublishedVersionUsedInSpec()
        }
        validateBuild()
    }

    protected void runInitWithPluginAsBuildInitSpecSupplier() {
        def initInvocation = ["-D${BuildInitSpecRegistry.BUILD_INIT_SPECS_PLUGIN_SUPPLIER}=$ecosystemPluginId:$DECLARATIVE_PROTOTYPE_VERSION",
                              "init",
                              "--type", "$projectSpecType"] as String[]

        result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(withDefaultArguments(initInvocation))
                .forwardOutput()
                .build()
    }

    protected void canBuildGeneratedProject() {
        result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(withDefaultArguments(buildTasks))
                .forwardOutput()
                .build()
    }

    protected String[] getBuildTasks() {
        return ["build"]
    }

    protected boolean shouldValidateLatestPublishedVersionUsedInSpec() {
        return true
    }

    protected void validateLatestPublishedVersionUsedInSpec() {
        def pendingProjectVersion = readProjectVersion(file("../../../../../../version.txt"))
        def lastPublishedVersion = previousVersion(pendingProjectVersion)
        def specVersion = readSpecVersion(projectDir.file("settings.gradle.dcl"))
        assert lastPublishedVersion == specVersion, "Expected spec to use last published version $lastPublishedVersion, but it was using $specVersion instead.  Please update this."
    }

    private String readProjectVersion(File versionFile) {
        def content = versionFile.text
        return content.trim()
    }

    protected String readSpecVersion(File settingsFile) {
        def content = settingsFile.text
        def matcher = content =~ /id\("org\.gradle\.experimental\..*"\)\.version\("([^"]+)"\)/
        return (matcher.find()) ? matcher.group(1) : null
    }

    private String previousVersion(String version) {
        if (version.endsWith("-SNAPSHOT")) {
            version = version - "-SNAPSHOT"
        }
        return version[0..-2] + (version[-1].toInteger() - 1)
    }
}
