package org.gradle.api.experimental.android

import org.gradle.integtests.fixtures.AbstractProjectInitSpecification
import org.gradle.testkit.runner.GradleRunner

class AndroidApplicationInitProjectSpec extends AbstractProjectInitSpecification {
    @Override
    String getPluginId() {
        return "org.gradle.experimental.android-ecosystem"
    }
}
