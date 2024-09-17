package org.gradle.api.experimental.kmp

import org.gradle.integtests.fixtures.AbstractProjectInitSpecification
import org.gradle.testkit.runner.GradleRunner

class KotlinApplicationInitProjectSpec extends AbstractProjectInitSpecification {
    @Override
    protected String getEcosystemPluginId() {
        return "org.gradle.experimental.kmp-ecosystem"
    }

    @Override
    protected String getProjectSpecType() {
        return "declarative-kotlin-(jvm)-application-project"
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
