package org.gradle.api.experimental.android

import org.gradle.integtests.fixtures.AbstractProjectInitSpecification

abstract class AbstractAndroidInitProjectSpec extends AbstractProjectInitSpecification {
    @Override
    protected String getEcosystemPluginId() {
        return "org.gradle.experimental.android-ecosystem"
    }
}
