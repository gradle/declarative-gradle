package org.gradle.api.experimental.plugin.internal;

import org.gradle.api.experimental.plugin.JavaGradlePluginBuildModel;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;

public class DefaultJaveGradlePluginBuildModel implements JavaGradlePluginBuildModel {
    private GradlePluginDevelopmentExtension extension;

    public GradlePluginDevelopmentExtension getDevelopmentExtension() {
        return extension;
    }

    public void setDevelopmentExtension(GradlePluginDevelopmentExtension extension) {
        this.extension = extension;
    }
}
