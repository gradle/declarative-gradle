package org.gradle.api.experimental.android.test.internal;

import com.android.build.api.dsl.TestExtension;
import org.gradle.api.experimental.android.test.AndroidTestBuildModel;

public class DefaultAndroidTestBuildModel implements AndroidTestBuildModel {
    private TestExtension testExtension;

    @Override
    public TestExtension getTestExtension() {
        return testExtension;
    }

    public void setTestExtension(TestExtension testExtension) {
        this.testExtension = testExtension;
    }
}
