package org.gradle.api.experimental.kmp

import org.gradle.integtests.fixtures.AbstractProjectInitSpecification

class KotlinApplicationInitProjectSpec extends AbstractProjectInitSpecification {
    @Override
    String getPluginId() {
        return "org.gradle.experimental.kmp-ecosystem"
    }
}
