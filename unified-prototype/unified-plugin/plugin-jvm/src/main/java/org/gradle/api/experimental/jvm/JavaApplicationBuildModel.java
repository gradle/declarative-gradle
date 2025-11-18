package org.gradle.api.experimental.jvm;

import org.gradle.api.experimental.common.HasCliExecutables;
import org.gradle.api.plugins.JavaApplication;

public interface JavaApplicationBuildModel extends JavaBuildModel, HasCliExecutables {
    JavaApplication getJavaApplicationExtension();
}
