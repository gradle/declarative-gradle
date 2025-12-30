package org.gradle.api.experimental.android

import spock.lang.Ignore

@Ignore("Temporarily disabled until new versions of prototype plugins are published")
class AndroidApplicationBuildInitSpec extends AbstractAndroidBuildInitSpec {
    @Override
    protected String getProjectSpecType() {
        return "android-application"
    }
}
