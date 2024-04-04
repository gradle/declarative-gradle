package org.gradle.api.experimental.android.library;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface AndroidLibraryBuildTypes {

    @Nested
    AndroidLibraryBuildType getDebug();

    @Configuring
    default void debug(Action<? super AndroidLibraryBuildType> action) {
        action.execute(getDebug());
    }

    @Nested
    AndroidLibraryBuildType getRelease();

    @Configuring
    default void release(Action<? super AndroidLibraryBuildType> action) {
        action.execute(getRelease());
    }
}
