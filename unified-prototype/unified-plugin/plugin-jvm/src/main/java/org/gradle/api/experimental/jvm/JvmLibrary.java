package org.gradle.api.experimental.jvm;

import org.gradle.api.Action;
import org.gradle.api.experimental.common.LibraryDependencies;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * The public DSL interface for a declarative JVM library.
 */
@Restricted
public interface JvmLibrary {

    @Nested
    LibraryDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super LibraryDependencies> action) {
        action.execute(getDependencies());
    }

    JvmTargetContainer getTargets();

    @Configuring
    default void targets(Action<? super JvmTargetContainer> action) {
        action.execute(getTargets());
    }
}
