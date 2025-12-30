package org.gradle.api.experimental.android.application;

import org.gradle.api.experimental.android.AndroidSoftwareBuildTypes;
import org.gradle.api.tasks.Nested;

public interface AndroidApplicationBuildTypes extends AndroidSoftwareBuildTypes {
    @Override
    @Nested
    AndroidApplicationBuildType getDebug();

    @Override
    @Nested
    AndroidApplicationBuildType getRelease();
}
