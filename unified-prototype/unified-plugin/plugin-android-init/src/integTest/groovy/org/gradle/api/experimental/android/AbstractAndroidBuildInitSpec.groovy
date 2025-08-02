package org.gradle.api.experimental.android

import org.gradle.integtests.fixtures.AbstractBuildInitSpecification

abstract class AbstractAndroidBuildInitSpec extends AbstractBuildInitSpecification {
    @Override
    protected String getEcosystemPluginId() {
        return "org.gradle.experimental.android-ecosystem-init"
    }

    @Override
    protected String[] getBuildTasks() {
        return ["assembleDebug"]
    }
}
