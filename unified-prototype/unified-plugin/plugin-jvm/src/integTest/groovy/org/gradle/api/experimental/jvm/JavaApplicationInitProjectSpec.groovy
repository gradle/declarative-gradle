//file:noinspection GroovyAssignabilityCheck
package org.gradle.api.experimental.jvm

import org.gradle.integtests.fixtures.AbstractProjectInitSpecification
import org.gradle.testkit.runner.GradleRunner

class JavaApplicationInitProjectSpec extends AbstractProjectInitSpecification {
    @Override
    protected String getEcosystemPluginId() {
        return "org.gradle.experimental.jvm-ecosystem"
    }

    @Override
    protected String getProjectSpecType() {
        return "declarative-java-application-project"
    }

    @Override
    protected void validateBuiltProject() {
        result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(":app:run")
                .forwardOutput()
                .build()

        assert result.output.contains("Hello World!")
    }
}
