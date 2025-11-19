package org.gradle.api.experimental.plugin;

import org.gradle.api.internal.plugins.BuildModel;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;

public interface JavaGradlePluginBuildModel extends BuildModel {
    GradlePluginDevelopmentExtension getDevelopmentExtension();
}
