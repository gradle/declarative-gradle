package org.gradle.api.experimental.kmp

import org.gradle.integtests.fixtures.AbstractBuildInitSpecification
import org.gradle.testkit.runner.GradleRunner

class KotlinApplicationInitProjectSpec extends AbstractBuildInitSpecification {
    @Override
    protected String getEcosystemPluginId() {
        return "org.gradle.experimental.kmp-ecosystem-init"
    }

    @Override
    protected String getProjectSpecType() {
        return "kotlin-application"
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
