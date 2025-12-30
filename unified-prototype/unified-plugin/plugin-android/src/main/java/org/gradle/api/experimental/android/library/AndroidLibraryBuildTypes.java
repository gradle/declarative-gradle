package org.gradle.api.experimental.android.library;

import org.gradle.api.experimental.android.AndroidSoftwareBuildTypes;
import org.gradle.api.tasks.Nested;

public interface AndroidLibraryBuildTypes extends AndroidSoftwareBuildTypes {
    @Override
    @Nested
    AndroidLibraryBuildType getDebug();

    @Override
    @Nested
    AndroidLibraryBuildType getRelease();
}
