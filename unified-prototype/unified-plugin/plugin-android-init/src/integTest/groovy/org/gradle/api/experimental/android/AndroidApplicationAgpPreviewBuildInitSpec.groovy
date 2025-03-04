package org.gradle.api.experimental.android

class AndroidApplicationAgpPreviewBuildInitSpec extends AbstractAndroidBuildInitSpec {
    @Override
    protected String getProjectSpecType() {
        return "android-application-agp-preview"
    }

    @Override
    protected boolean shouldValidateLatestPublishedVersionUsedInSpec() {
        return false // Specs using official AGP versions don't use the prototype plugins
    }
}