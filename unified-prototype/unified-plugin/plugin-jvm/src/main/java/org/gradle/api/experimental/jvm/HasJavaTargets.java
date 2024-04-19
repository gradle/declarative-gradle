package org.gradle.api.experimental.jvm;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * A component that is built for multiple Java versions.
 */
@Restricted
public interface HasJavaTargets {

    @Nested
    JvmTargetContainer getTargets();

    @Configuring
    default void targets(Action<? super JvmTargetContainer> action) {
        action.execute(getTargets());
    }
}
