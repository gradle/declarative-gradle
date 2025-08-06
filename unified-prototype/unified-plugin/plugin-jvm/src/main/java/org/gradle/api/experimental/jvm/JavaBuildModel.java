package org.gradle.api.experimental.jvm;

import org.gradle.api.internal.plugins.BuildModel;
import org.gradle.api.plugins.JavaPluginExtension;

public interface JavaBuildModel extends BuildModel {
    JavaPluginExtension getJavaPluginExtension();
}
