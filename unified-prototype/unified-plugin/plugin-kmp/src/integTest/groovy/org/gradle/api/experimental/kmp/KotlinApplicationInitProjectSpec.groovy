package org.gradle.api.experimental.kmp

import org.gradle.integtests.fixtures.AbstractProjectInitSpecification

class KotlinApplicationInitProjectSpec extends AbstractProjectInitSpecification {
    @Override
    String getEcosytemPluginId() {
        return "org.gradle.experimental.kmp-ecosystem"
    }

    @Override
    String getProjectSpecType() {
        return "declarative-kotlin-(jvm)-application-project"
    }
}
