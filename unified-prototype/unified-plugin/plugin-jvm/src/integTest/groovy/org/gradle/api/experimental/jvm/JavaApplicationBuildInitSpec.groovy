//file:noinspection GroovyAssignabilityCheck
package org.gradle.api.experimental.jvm

import org.gradle.integtests.fixtures.AbstractBuildInitSpecification
import org.gradle.testkit.runner.GradleRunner

class JavaApplicationBuildInitSpec extends AbstractBuildInitSpecification {
    @Override
    protected String getEcosystemPluginId() {
        return "org.gradle.experimental.jvm-ecosystem-init"
    }

    @Override
    protected String getProjectSpecType() {
        return "java-application"
    }

    @Override
    protected void validateBuild() {
        result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(":app:run")
                .forwardOutput()
                .build()

        assert result.output.contains("Hello World!")
    }
}
