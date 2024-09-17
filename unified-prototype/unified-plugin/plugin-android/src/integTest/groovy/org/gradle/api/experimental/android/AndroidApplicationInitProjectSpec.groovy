package org.gradle.api.experimental.android

import org.gradle.testkit.runner.GradleRunner

class AndroidApplicationInitProjectSpec extends AbstractAndroidInitProjectSpec {
    @Override
    protected String getProjectSpecType() {
        return "declarative-android-application-project"
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
