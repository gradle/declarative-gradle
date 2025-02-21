package org.gradle.api.experimental.android.library;

import org.gradle.api.Action;
import org.gradle.api.experimental.android.AndroidSoftwareBuildType;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

public interface AndroidLibraryBuildType extends AndroidSoftwareBuildType {
    @Override
    @Nested
    AndroidLibraryDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super AndroidLibraryDependencies> action) {
        action.execute(getDependencies());
    }
}
