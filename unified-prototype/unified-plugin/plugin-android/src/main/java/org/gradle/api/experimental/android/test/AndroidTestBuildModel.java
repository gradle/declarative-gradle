package org.gradle.api.experimental.android.test;

import com.android.build.api.dsl.TestExtension;
import org.gradle.api.internal.plugins.BuildModel;

public interface AndroidTestBuildModel extends BuildModel {
    public TestExtension getTestExtension();
}
