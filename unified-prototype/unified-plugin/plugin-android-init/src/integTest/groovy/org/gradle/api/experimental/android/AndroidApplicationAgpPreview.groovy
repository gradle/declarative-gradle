package org.gradle.api.experimental.android

import spock.lang.Ignore

@Ignore("Need to have following fix in Gradle: https://github.com/gradle/gradle/pull/32512") // TODO
class AndroidApplicationAgpPreview extends AbstractAndroidBuildInitSpec {
    @Override
    protected String getProjectSpecType() {
        return "android-application-agp-preview"
    }
}