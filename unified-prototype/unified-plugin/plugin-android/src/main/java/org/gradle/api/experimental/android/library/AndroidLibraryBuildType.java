package org.gradle.api.experimental.android.library;

import org.gradle.api.experimental.android.AndroidSoftwareBuildType;
import org.gradle.api.tasks.Nested;

public interface AndroidLibraryBuildType extends AndroidSoftwareBuildType {
    @Override
    @Nested
    AndroidLibraryDependencies getDependencies();
}
