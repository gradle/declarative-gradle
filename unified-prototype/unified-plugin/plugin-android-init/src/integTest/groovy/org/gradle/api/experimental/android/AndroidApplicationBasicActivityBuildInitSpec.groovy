package org.gradle.api.experimental.android

import spock.lang.Ignore

@Ignore("Temporarily disabled until new versions of prototype plugins are published")
class AndroidApplicationBasicActivityBuildInitSpec extends AbstractAndroidBuildInitSpec {
    @Override
    String getProjectSpecType() {
        return "android-application-basic-activity"
    }
}
