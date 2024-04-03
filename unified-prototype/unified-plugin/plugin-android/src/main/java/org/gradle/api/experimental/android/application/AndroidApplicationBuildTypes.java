package org.gradle.api.experimental.android.application;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface AndroidApplicationBuildTypes {

    @Nested
    AndroidApplicationBuildType getDebug();

    @Configuring
    default void debug(Action<? super AndroidApplicationBuildType> action) {
        action.execute(getDebug());
    }

    @Nested
    AndroidApplicationBuildType getRelease();

    @Configuring
    default void release(Action<? super AndroidApplicationBuildType> action) {
        action.execute(getRelease());
    }
}
