package org.gradle.api.experimental.android.library;

import org.gradle.api.Action;
import org.gradle.api.experimental.android.AndroidSoftwareBuildTypes;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

public interface AndroidLibraryBuildTypes extends AndroidSoftwareBuildTypes {
    @Override
    @Nested
    AndroidLibraryBuildType getDebug();

    @Configuring
    default void debug(Action<? super AndroidLibraryBuildType> action) {
        action.execute(getDebug());
    }

    @Override
    @Nested
    AndroidLibraryBuildType getRelease();

    @Configuring
    default void release(Action<? super AndroidLibraryBuildType> action) {
        action.execute(getRelease());
    }
}
