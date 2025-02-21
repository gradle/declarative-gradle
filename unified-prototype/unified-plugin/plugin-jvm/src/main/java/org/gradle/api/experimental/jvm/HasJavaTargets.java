package org.gradle.api.experimental.jvm;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;

/**
 * A component that is built for multiple Java versions.
 */
public interface HasJavaTargets {

    @Nested
    JvmTargetContainer getTargets();

    @Configuring
    default void targets(Action<? super JvmTargetContainer> action) {
        action.execute(getTargets());
    }
}
