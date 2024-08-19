package org.gradle.api.experimental.android

import org.gradle.integtests.fixtures.AbstractProjectInitSpecification

class AndroidApplicationInitProjectSpec extends AbstractProjectInitSpecification {
    @Override
    String getPluginId() {
        return "org.gradle.experimental.android-ecosystem"
    }
}
