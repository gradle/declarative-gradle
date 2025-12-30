package org.gradle.api.experimental.android

import spock.lang.Ignore

@Ignore("Temporarily disabled until new versions of prototype plugins are published")
class AndroidApplicationEmptyActivityBuildInitSpec extends AbstractAndroidBuildInitSpec {
    @Override
    String getProjectSpecType() {
        return "android-application-empty-activity"
    }
}
