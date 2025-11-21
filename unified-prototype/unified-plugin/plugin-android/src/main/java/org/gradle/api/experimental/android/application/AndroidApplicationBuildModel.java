package org.gradle.api.experimental.android.application;

import com.android.build.api.dsl.ApplicationExtension;
import org.gradle.api.internal.plugins.BuildModel;

public interface AndroidApplicationBuildModel extends BuildModel {
    ApplicationExtension getApplicationExtension();
}
