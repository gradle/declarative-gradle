package org.gradle.api.experimental.android.application;

import org.gradle.api.Action;
import org.gradle.api.experimental.android.AndroidSoftwareBuildTypes;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

public interface AndroidApplicationBuildTypes extends AndroidSoftwareBuildTypes {
    @Override
    @Nested
    AndroidApplicationBuildType getDebug();

    @Configuring
    default void debug(Action<? super AndroidApplicationBuildType> action) {
        action.execute(getDebug());
    }

    @Override
    @Nested
    AndroidApplicationBuildType getRelease();

    @Configuring
    default void release(Action<? super AndroidApplicationBuildType> action) {
        action.execute(getRelease());
    }
}
