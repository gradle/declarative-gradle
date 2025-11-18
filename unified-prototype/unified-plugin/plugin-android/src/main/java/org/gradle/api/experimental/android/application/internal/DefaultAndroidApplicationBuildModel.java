package org.gradle.api.experimental.android.application.internal;

import com.android.build.api.dsl.ApplicationExtension;
import org.gradle.api.experimental.android.application.AndroidApplicationBuildModel;

abstract public class DefaultAndroidApplicationBuildModel implements AndroidApplicationBuildModel {
    private ApplicationExtension applicationExtension;

    @Override
    public ApplicationExtension getApplicationExtension() {
        return applicationExtension;
    }

    public void setApplicationExtension(ApplicationExtension applicationExtension) {
        this.applicationExtension = applicationExtension;
    }
}
